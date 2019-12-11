package funkt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LazyTest {

    @Test
    internal fun lazyCall() {
        var callCount = 0
        val lazyValue = Lazy {
            callCount += 1
            callCount
        }
        assertEquals(0, callCount)
        assertEquals(1, lazyValue())
        assertEquals(1, callCount)
        assertEquals(1, lazyValue())
        assertEquals(1, callCount)
    }

    @Test
    internal fun mapLazyValues() {
        var callCount = 0
        val lazyValue = Lazy { 3 }.map {
            callCount += 1
            it * it
        }
        assertEquals(0, callCount)
        assertEquals(9, lazyValue())
        assertEquals(1, callCount)
    }
}
