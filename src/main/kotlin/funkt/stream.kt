package funkt

sealed class Stream<out A> {

    fun uncons(): Option<Pair<A, Lazy<Stream<A>>>> = when (this) {
        is Empty -> Option()
        is Cons -> Option(head to tail)
    }

    internal object Empty : Stream<Nothing>()

    internal class Cons<A>(val head: A, val tail: Lazy<Stream<A>>) : Stream<A>()

    companion object {

        fun <A> iterate(a: A, f: (A) -> A): Stream<A> = Cons(a, Lazy { iterate(f(a), f) })
    }
}
