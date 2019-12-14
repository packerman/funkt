package funkt

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ListTest {

    @Test
    internal fun createLists() {
        assertTrue(List<Any>().isEmpty())
        assertFalse(List(1, 2, 3).isEmpty())
    }

    @Test
    internal fun canAddElements() {
        assertFalse(List<Int>().cons(1).isEmpty())
    }

    @Test
    internal fun canFindElements() {
        assertEquals(Option(2), List(1, 2, 3).find { it > 1 })
        assertEquals(Option<Int>(), List(1, 2, 3).find { it > 5 })
        assertEquals(Option(6), List(1, 2, 3).cons(6).find { it > 5 })
    }

    @Test
    internal fun assocValues() {
        assertEquals(Option<Int>(), List("a" to 1).lookup("b"))
        assertEquals(Option(2), List("a" to 1).assoc("b", 2).lookup("b"))
    }

    @Test
    internal fun testIterateList() {
        assertEquals(listOf<Any>(), List<Any>().toList())
        assertEquals(listOf(1, 2, 3), List(1, 2, 3).toList())
    }

    @Test
    internal fun testToString() {
        assertEquals("[]", List<Int>().toString())
        assertEquals("[1]", List(1).toString())
        assertEquals("[1, 2]", List(1, 2).toString())
    }

    @Test
    internal fun listToStream() {
        assertEquals(
            listOf(1, 2, 3), List(1, 2, 3).toStream()
                .asIterable().toList()
        )
    }

    @Test
    internal fun testLength() {
        assertEquals(0, List<Nothing>().length)
        assertEquals(3, List(1, 2, 3).length)
    }
}
