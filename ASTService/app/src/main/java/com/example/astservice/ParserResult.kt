package com.example.astservice

enum class ParserResultType {
    Content, Container
}

class ParserResult(
    val type: ParserResultType = ParserResultType.Content,
    val name: String = "",
    val status: Boolean = true,
    val index: Int = 0,
    val expected: List<String> = listOf(),
    val children: List<ParserResult>,
    val value: String,
) {}

fun makeContainer(index: Int, children: List<ParserResult>): ParserResult {
    return ParserResult(
        type = ParserResultType.Container,
        name = "",
        status = true,
        index = index,
        expected = listOf(),
        children = children,
        value = "",
    )
}

fun makeSuccess(index: Int, value: String): ParserResult {
    return ParserResult(
        type = ParserResultType.Content,
        name = "",
        status = true,
        index = index,
        expected = listOf(),
        children = listOf(),
        value = value,
    )
}

fun makeFailure(index: Int, expected: List<String>): ParserResult {
    return ParserResult(
        type = ParserResultType.Content,
        name = "",
        status = false,
        index = index,
        expected = expected,
        children = listOf(),
        value = "",
    )
}

fun mergeResults(first: ParserResult, last: ParserResult): ParserResult {
    return ParserResult(
        type = first.type,
        name = first.name,
        status = first.status,
        index = last.index,
        expected = first.expected,
        children = first.children,
        value = first.value,
    )
}

fun stringify(result: ParserResult): String {
    fun tab(buffer: String, n: Int): String {
        return buffer + "  ".repeat(n)
    }

    fun loop(result: ParserResult, buffer: String, nest: Int): String {
        var buffer = tab(buffer, nest)
        if (result.status && result.name != "") {
            buffer = buffer + result.name + " "
        }
        buffer += "{\n"

        buffer = tab(buffer, nest+1)
        buffer += "\"status\": " + result.status + ",\n"
        buffer = tab(buffer, nest+1)
        buffer += "\"index\": " + result.index + ",\n"

        if (result.type == ParserResultType.Container) {
            buffer = tab(buffer, nest+1)
            buffer += "\"children\": [\n"
            val children = result.children
            for (child in children) {
                buffer = loop(child, buffer, nest+2)
            }
            buffer = tab(buffer, nest+1)
            buffer += "],\n"
        } else {
            buffer = tab(buffer, nest+1)
            buffer += "\"value\": \"" + result.value + "\",\n"

            if (!result.status) {
                val expected = result.expected
                buffer = tab(buffer, nest+1)
                buffer += "\"expected\": [\n"
                for (elm in expected) {
                    buffer = tab(buffer, nest+2)
                    buffer += "\"" + elm + "\",\n"
                }
                buffer = tab(buffer, nest+1)
                buffer += "],\n"
            }
        }

        buffer = tab(buffer, nest)
        buffer += "}"

        if (nest != 0) {
            buffer += ","
        }
        buffer += "\n"

        return buffer
    }

    return loop(result, "", 0)
}



