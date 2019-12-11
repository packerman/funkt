package funkt

import funkt.Stream.Companion.concat
import funkt.Stream.Companion.cons
import funkt.Stream.Companion.drop
import funkt.Stream.Companion.iterate
import funkt.Stream.Companion.repeat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StreamTest {

    @Test
    internal fun createStreams() {
        assertTrue(Stream<Int>().isEmpty())
        assertFalse(Stream(1, 2, 3).isEmpty())
        assertEquals(listOf(1, 2, 3), Stream(1, 2, 3).asIterable().toList())
    }

    @Test
    internal fun generateStreamByIteration() {
        val s = iterate(0) { it + 1 }

        s.unCons().flatMap { (h0, t0) ->
            assertEquals(0, h0)
            t0().unCons().flatMap { (h1, t1) ->
                assertEquals(1, h1)
                t1().unCons().flatMap { (h2, t2) ->
                    assertEquals(2, h2)
                    t2().unCons().map { (h3, _) ->
                        assertEquals(3, h3)
                    }
                }
            }
        }.getOrElse { fail("Not enough elements in stream") }
    }

    @Test
    internal fun dropStream() {
        val s = iterate(0) { it + 1 }

        s.drop(1000000).unCons().map { (h, _) ->
            assertEquals(1000000, h)
        }.getOrElse { fail("Not enough elements in stream") }

        drop(s, 1000000).unCons().map { (h, _) ->
            assertEquals(1000000, h)
        }.getOrElse { fail("Not enough elements in stream") }
    }

    @Test
    internal fun takeFirstAndIterate() {
        val list = iterate(1) { it + 2 }
            .take(5)
            .asIterable()
            .toList()

        assertEquals(listOf(1, 3, 5, 7, 9), list)
    }

    @Test
    internal fun shouldCreateInfiniteStreams() {
        fun ones(): Stream<Int> = cons(1) { ones() }

        assertEquals(listOf(1, 1, 1, 1, 1), ones().take(5).asIterable().toList())
    }

    @Test
    internal fun shouldZipStreams() {
        fun fib(): Stream<Int> = cons(1) {
            cons(1) {
                fib().zip(fib().drop(1)) { a, b -> a + b }
            }
        }
        assertEquals(listOf(1, 1, 2, 3, 5, 8, 13, 21, 34, 55), fib().take(10).asIterable().toList())
    }

    @Test
    internal fun createStreamByRepeating() {
        assertEquals(listOf(2, 2, 2, 2, 2, 2), repeat(2).take(6).asIterable().toList())
    }

    @Test
    internal fun concatStreams() {
        assertEquals(
            listOf(1, 2, 3, 4, 5, 6), Stream(1, 2, 3).concat(Stream(4, 5, 6))
                .asIterable().toList()
        )
        assertEquals(
            listOf(1, 2, 3, 4, 5, 6), concat(Stream(1, 2, 3), Stream(4, 5, 6))
                .asIterable().toList()
        )

        assertEquals(
            listOf(1, 1, 1, 1, 1, 2, 2, 2, 2, 2),
            repeat(1).take(1000000).concat(repeat(2).take(1000000)).drop(999995).take(10)
                .asIterable().toList()
        )
    }
}
