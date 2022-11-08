package com.example.astservice

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class JsonParserTest {
    @Test
    fun basic_object_parse_isCorrect() {
        val jsonString = "{ \"key1\": \"value1\" }"
        val ast = jsonParser()(jsonString, 0)

        println(stringify(ast))

        assertTrue(ast.status)
        assertEquals(ast.index, jsonString.length)
    }

    @Test
    fun basic_array_parse_isCorrect() {
        val jsonString = "[1, 2, 3]"
        val ast = jsonParser()(jsonString, 0)

        println(stringify(ast))

        assertTrue(ast.status)
        assertEquals(ast.index, jsonString.length)
    }

    @Test
    fun string_isCorrect() {
        val jsonString = "\"hogehuga\""
        val dquote = str("\"")
        val _string = regex(Regex(".+(?=\")")).wrap(dquote, dquote).name("String")
        val ast = _string(jsonString, 0)

        println(stringify(ast))

        assertTrue(ast.status)
        assertEquals(ast.index, jsonString.length)
    }
}