package funkt

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OptionTest {

    @Test
    internal fun createOptions() {
        assertTrue(Option<Any>().isEmpty())
        assertTrue(Option(null).isEmpty())
        assertFalse(Option(5).isEmpty())
    }

    @Test
    internal fun getValueFromOption() {
        assertEquals(5, Option(5).getOrElse(1))
        assertEquals(1, Option<Int>().getOrElse(1))
    }

    @Test
    internal fun getValueFromOptionLazily() {
        assertEquals(5, Option(5).getOrElse { throw RuntimeException() })
    }

    @Test
    internal fun mapOption() {
        assertEquals(Option(9), Option(3).map { it * it })
        assertEquals(Option<Int>(), Option<Int>().map { it * it })
    }

    @Test
    internal fun flatMapOptions() {
        assertEquals(Option<Int>(), Option<Int>().flatMap { Option(it * it) })
        assertEquals(Option(9), Option(3).flatMap { Option(it * it) })
        assertEquals(Option<Int>(), Option(3).flatMap { Option<Int>() })
    }
}
