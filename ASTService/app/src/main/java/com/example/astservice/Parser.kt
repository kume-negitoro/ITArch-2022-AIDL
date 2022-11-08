package com.example.astservice

typealias Parser = (target: String, index: Int) -> ParserResult
typealias ParserResultMapper = (result: ParserResult) -> ParserResult

fun succeed(value: String): Parser {
    return fun (_: String, index: Int): ParserResult {
        return makeSuccess(index, value)
    }
}

fun lazyp(f: () -> Parser): Parser {
    return fun (target: String, index: Int): ParserResult {
        return f()(target, index)
    }
}

fun str(expected: String): Parser {
    return fun (target: String, index: Int): ParserResult {
        val length = expected.length

        if (index + length > target.length) {
            return makeFailure(index, listOf(expected))
        }

        return if (target.substring(index, index + length) == expected) {
            makeSuccess(index + length, expected)
        } else {
            makeFailure(index, listOf(expected))
        }
    }
}

fun many(parser: Parser): Parser {
    return fun (target: String, index: Int): ParserResult {
        var index = index
        var children = mutableListOf<ParserResult>()

        while (true) {
            val parsed = parser(target, index)

            if (!parsed.status) break

            index = parsed.index
            children.add(parsed)
        }

        return makeContainer(index, children)
    }
}

fun alt(vararg parsers: Parser): Parser {
    return fun (target: String, index: Int): ParserResult {
        var expected = mutableListOf<String>()

        for (parser in parsers) {
            val parsed = parser(target, index)

            if(parsed.status) return parsed

            expected.add(parsed.expected.joinToString(", "))
        }

        return makeFailure(index, expected)
    }
}

fun seq(vararg parsers: Parser): Parser {
    return fun (target: String, index: Int): ParserResult {
        var index = index
        var children = mutableListOf<ParserResult>()

        for (parser in parsers) {
            val parsed = parser(target, index)

            if (!parsed.status) return parsed

            index = parsed.index
            children.add(parsed)
        }

        return makeContainer(index, children)
    }
}

fun regex(reg: Regex): Parser {
    return fun (target: String, index: Int): ParserResult {
        val matched = reg.find(target.substring(index))

        return if (matched != null) {
            makeSuccess(index + matched.value.length, matched.value)
        } else {
            makeFailure(index, listOf(reg.pattern))
        }
    }
}

fun map(mapper: ParserResultMapper, parser: Parser): Parser {
    return fun (target: String, index: Int): ParserResult {
        return mapper(parser(target, index))
    }
}

fun seqMap(mapper: ParserResultMapper, vararg parsers: Parser): Parser {
    return map(mapper, seq(*parsers))
}

fun sepBy1(parser: Parser, separator: Parser): Parser {
    val pairs = many(seq(separator, parser))
    return seqMap(fun (result: ParserResult): ParserResult {
        if (!result.status) return result

        val results = mutableListOf(result.children[0])
        results.addAll(result.children[1].children.map{ it.children[1] })

        return makeContainer(results.last().index, results)
    }, parser, pairs)
}

fun sepBy(parser: Parser, separator: Parser): Parser {
    return alt(sepBy1(parser, separator), succeed(""))
}

fun Parser.or(right: Parser): Parser {
    return alt(this, right)
}

fun Parser.map(mapper: ParserResultMapper): Parser {
    return map(mapper, this)
}

fun Parser.wrap(edge: Parser): Parser {
    return this.wrap(edge, edge)
}
fun Parser.wrap(left: Parser, right: Parser): Parser {
    return seqMap(fun (result: ParserResult): ParserResult {
        if (!result.status) return result

        return mergeResults(result.children[1], result.children[2])
    }, left, this, right)
}

fun Parser.then(next: Parser): Parser {
    return seqMap(fun (result: ParserResult): ParserResult {
        if (!result.status) return result

        return result.children[1]
    }, this, next)
}

fun Parser.skip(next: Parser): Parser {
    return seqMap(fun (result: ParserResult): ParserResult {
        if (!result.status) return result

        return mergeResults(result.children[0], result.children[1])
    }, this, next)
}

fun Parser.name(name: String): Parser {
    return map(fun (result: ParserResult): ParserResult {
        if (!result.status) return result

        return ParserResult(
            type = result.type,
            name = name,
            status = result.status,
            index = result.index,
            expected = result.expected,
            children = result.children,
            value = result.value,
        )
    }, this)
}
