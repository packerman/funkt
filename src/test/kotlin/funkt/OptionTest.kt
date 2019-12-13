package funkt

import funkt.Option.Companion.some
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OptionTest {

    @Test
    internal fun createOptions() {
        assertTrue(Option<Any>().isEmpty())
        assertTrue(Option(null).isEmpty())
        assertFalse(Option(5).isEmpty())
        assertEquals(Option(5), some(5))
    }

    @Test
    internal fun getValueFromOption() {
        assertEquals(5, Option(5).getOrElse(1))
        assertEquals(1, Option<Int>().getOrElse(1))
    }

    @Test
    internal fun testOrElse() {
        assertEquals(Option<Any>(), Option<Any>().orElse { Option() })
        assertEquals(Option(5), Option(5).orElse { Option(1) })
        assertEquals(Option(5), Option(5).orElse { Option() })
        assertEquals(Option(5), Option<Int>().orElse { Option(5) })
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

    @Test
    internal fun callForEach() {
        var callCount = 0
        Option<Int>().forEach { callCount += 1 }
        assertEquals(0, callCount)
        Option(3).forEach { callCount += 1 }
        assertEquals(1, callCount)
    }
}
