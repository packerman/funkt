package funkt

import funkt.FunctionTest.EvenOdd.even
import funkt.FunctionTest.EvenOdd.odd
import funkt.Trampoline.Companion.trampoline
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FunctionTest {

    object EvenOdd {

        fun odd(n: Int): Trampoline<Boolean> =
            if (n == 0) Trampoline(false)
            else Trampoline { even(n - 1) }

        fun even(n: Int): Trampoline<Boolean> =
            if (n == 0) Trampoline(true)
            else Trampoline { odd(n - 1) }
    }

    @Test
    internal fun trampolineFunction() {
        assertTrue(trampoline { even(1000000) })
        assertFalse(trampoline { even(1000001) })

        assertFalse(trampoline { odd(1000000) })
        assertTrue(trampoline { odd(1000001) })
    }

    @Test
    internal fun curryFunction() {
        val f = curry<Int, Int, Int> { a, b -> a + b }
        assertEquals(8, f(5)(3))
    }

    @Test
    internal fun flipFunctions() {
        val f = { a: Int, b: Int -> a - b }
        val curried = curry(f)

        assertEquals(2, f(5, 3))
        assertEquals(-2, flip(f)(5, 3))
        assertEquals(2, curried(5)(3))
        assertEquals(-2, flip(curried)(5)(3))
    }

    @Test
    internal fun partialApplyFunctions() {
        val f = { a: Int, b: Int -> a + b }
        val p = partial(f, 2)
        assertEquals(5, p(3))
    }
}
