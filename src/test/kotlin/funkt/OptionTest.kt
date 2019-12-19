package funkt

import funkt.Option.Companion.cond
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

    @Test
    internal fun optionToStream() {
        assertEquals(Stream<Nothing>(), Option<Nothing>().toStream())
        assertEquals(listOf(5), Option(5).toStream().asIterable().toList())
    }

    @Test
    internal fun testCond() {
        assertEquals(Option<Any>(), cond(Option<Any>()))
        assertEquals(Option(5), cond(Option(5)))
        assertEquals(Option(3), cond(Option(3), { Option(5) }))
        assertEquals(Option(1), cond(Option(), { Option(1) }))
        assertEquals(Option(2), cond(Option(), { Option<Int>() }, { Option(2) }))
        assertEquals(Option(6), cond(Option(), { Option(6) }, { error("Error") }))
    }
}
