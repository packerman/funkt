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

    @Test
    internal fun testFoldLeft() {
        assertEquals(0, List<Int>().foldLeft(0) { s, a -> s + a })
        assertEquals(6, List(1, 2, 3).foldLeft(0) { s, a -> s + a })
        assertEquals(List(3, 2, 1),
            List(1, 2, 3).foldLeft(List<Int>()) { l, a -> l.cons(a) })
    }

    @Test
    internal fun testReverse() {
        assertEquals(List(3, 2, 1), List(1, 2, 3).reverse())
    }

    @Test
    internal fun mapElements() {
        assertEquals(listOf(1, 4, 9, 16, 25),
            List(1, 2, 3, 4, 5).map { it * it }.toList()
        )
    }

    @Test
    internal fun filterElements() {
        assertEquals(
            listOf(4, 3, 4, 3),
            List(4, -6, 3, 4, 0, -3, -2, 3).filter { it > 0 }.toList()
        )
    }

    @Test
    internal fun removeElementsNotSatisfyingPredicate() {
        assertEquals(
            listOf(4, 3, 4, 0, 3),
            List(4, -6, 3, 4, 0, -3, -2, 3).remove { it < 0 }.toList()
        )
    }
}
