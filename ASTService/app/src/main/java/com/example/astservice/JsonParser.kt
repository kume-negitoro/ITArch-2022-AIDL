package com.example.astservice

fun jsonParser(): Parser {
    val lbrace = str("{")
    val rbrace = str("}")
    val lbracket = str("[")
    val rbracket = str("]")
    val comma = str(",")
    val colon = str(":")
    val dquote = str("\"")

    val _null = str("null").name("Null")
    val _true = str("true").name("True")
    val _false = str("false").name("False")
    val _string = regex(Regex(".+?(?=\")")).wrap(dquote).name("String")
    val _number = regex(Regex("-?(0|[1-9][0-9]*)([.][0-9]+)?([eE][+-]?[0-9]+)?")).name("Number")

    val white = str(" ")
    val newline = str("\n")
    val optWhite = many(alt(white, newline))

    lateinit var _object: Parser
    lateinit var _array: Parser

    val value = lazyp{ alt(_array, _object, _null, _true, _false, _string, _number).wrap(optWhite) }

    val kvPair = seq(_string.skip(colon.wrap(optWhite)), value).wrap(optWhite).name("KeyValuePair")
    _object = sepBy(kvPair, comma).wrap(lbrace.wrap(optWhite), rbrace.wrap(optWhite)).name("Object")
    _array = sepBy(value, comma).wrap(lbracket.wrap(optWhite), rbracket.wrap(optWhite)).name("Array")

    return value
}
