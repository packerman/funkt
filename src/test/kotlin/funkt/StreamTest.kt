package funkt

import funkt.Stream.Companion.concat
import funkt.Stream.Companion.cons
import funkt.Stream.Companion.drop
import funkt.Stream.Companion.interleave
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

        s.drop(100000).unCons().map { (h, _) ->
            assertEquals(100000, h)
        }.getOrElse { fail("Not enough elements in stream") }

        drop(s, 100000).unCons().map { (h, _) ->
            assertEquals(100000, h)
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
            repeat(1).take(100000).concat(repeat(2).take(100000)).drop(99995).take(10)
                .asIterable().toList()
        )
    }

    @Test
    internal fun mapValues() {
        assertEquals(listOf(1, 4, 9, 16, 25, 36, 49, 64, 81),
            iterate(1) { it + 1 }.map { it * it }
                .take(9).asIterable().toList())

        assertEquals(listOf(9999400009, 9999600004, 9999800001, 10000000000),
            iterate(1L) { it + 1 }.map { it * it }
                .take(100000).drop(99996)
                .asIterable().toList())
    }

    @Test
    internal fun flatMapStreams() {
        assertEquals(listOf(1, 2, 2, 3, 3, 3), Stream(1, 2, 3).flatMap { repeat(it).take(it) }.asIterable().toList())

        assertEquals(listOf(1, 2, 2, 3, 3, 3, 4, 4, 4, 4),
            iterate(1) { it + 1 }.flatMap { repeat(it).take(it) }.take(10)
                .asIterable().toList()
        )

        assertEquals(listOf(447, 447, 447, 447, 447, 447, 447, 447, 447, 447),
            iterate(1) { it + 1 }.flatMap { repeat(it).take(it) }.take(100000).drop(99990)
                .asIterable().toList()
        )
    }

    @Test
    internal fun interleaveStreams() {
        assertEquals(
            listOf(1, 2, 1, 2, 1, 2, 1, 2, 1, 2),
            interleave(Stream(1, 1, 1, 1, 1), Stream(2, 2, 2, 2, 2))
                .asIterable().toList()
        )

        assertEquals(
            listOf(1, 2, 1, 2, 1, 2, 1, 2, 1, 2),
            Stream(1, 1, 1, 1, 1).interleave(Stream(2, 2, 2, 2, 2))
                .asIterable().toList()
        )

        assertEquals(
            listOf(1, 2, 1, 2, 1, 2, 1, 2, 1, 2),
            interleave(repeat(1), repeat(2))
                .take(100000).drop(99990)
                .asIterable().toList()
        )
    }

    @Test
    internal fun sieveOfEratosthenes() {
        fun sieve(s: Stream<Int>): Stream<Int> = s.unCons().map { (h, t) ->
            cons(h, t.map { sieve(it.filter { n -> n % h != 0 }) })
        }.getOrElse(Stream())
        assertEquals(
            listOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 29),
            sieve(iterate(2) { it + 1 }).take(10)
                .asIterable().toList()
        )
    }
}
