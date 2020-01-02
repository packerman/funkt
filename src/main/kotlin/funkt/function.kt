package funkt

sealed class Trampoline<A> {

    internal class Return<A>(internal val value: A) : Trampoline<A>()

    internal class Continue<A>(internal val function: () -> Trampoline<A>) : Trampoline<A>()

    companion object {

        operator fun <A> invoke(value: A): Trampoline<A> = Return(value)

        operator fun <A> invoke(function: () -> Trampoline<A>): Trampoline<A> = Continue(function)

        tailrec fun <A> trampoline(function: () -> Trampoline<A>): A =
            when (val result = function()) {
                is Return -> result.value
                is Continue -> trampoline(result.function)
            }
    }
}

fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C = { a -> { b -> f(a, b) } }
