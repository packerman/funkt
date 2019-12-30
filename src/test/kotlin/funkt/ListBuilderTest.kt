package funkt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ListBuilderTest {

    @Test
    internal fun buildList() {
        val list = buildList<Int> {
            add(5)
            add(3)
            add(2)
        }

        assertEquals(listOf(5, 3, 2), list.toList())
    }

    @Test
    internal fun buildEmptyList() {
        val list = buildList<Any> {
        }

        assertEquals(listOf<Any>(), list.toList())
    }
}
