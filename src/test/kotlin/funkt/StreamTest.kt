package funkt

import funkt.Stream.Companion.iterate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

internal class StreamTest {

    @Test
    internal fun generateStreamByIteration() {
        val s = iterate(0) { it + 1 }

        s.uncons().flatMap { (h0, t0) ->
            assertEquals(0, h0)
            t0().uncons().flatMap { (h1, t1) ->
                assertEquals(1, h1)
                t1().uncons().flatMap { (h2, t2) ->
                    assertEquals(2, h2)
                    t2().uncons().map { (h3, _) ->
                        assertEquals(3, h3)
                    }
                }
            }
        }.getOrElse { fail("Not enough elements in stream") }
    }
}
