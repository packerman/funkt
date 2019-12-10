package funkt

sealed class Stream<out A> {

    fun unCons(): Option<Pair<A, Lazy<Stream<A>>>> = when (this) {
        is Empty -> Option()
        is Cons -> Option(head to tail)
    }

    fun take(n: Int): Stream<A> =
        if (n <= 0) Empty else when (this) {
            is Empty -> Empty
            is Cons -> Cons(head, Lazy { tail().take(n - 1) })
        }

    fun drop(n: Int): Stream<A> = drop(this, n)

    fun <B, C> zip(other: Stream<B>, f: (A, B) -> C): Stream<C> = when (this) {
        is Empty -> Empty
        is Cons -> when (other) {
            is Empty -> Empty
            is Cons -> Cons(f(head, other.head), Lazy { tail().zip(other.tail(), f) })
        }
    }

    fun asIterable(): Iterable<A> = object : Iterable<A> {
        override fun iterator(): Iterator<A> = StreamIterator(this@Stream)
    }

    internal object Empty : Stream<Nothing>()

    internal class Cons<A>(val head: A, val tail: Lazy<Stream<A>>) : Stream<A>()

    companion object {

        fun <A> cons(head: A, tail: () -> Stream<A>): Stream<A> = Cons(head, Lazy(tail))

        fun <A> iterate(a: A, f: (A) -> A): Stream<A> = Cons(a, Lazy { iterate(f(a), f) })

        tailrec fun <A> drop(stream: Stream<A>, n: Int): Stream<A> =
            if (n <= 0) stream else when (stream) {
                is Empty -> Empty
                is Cons -> drop(stream.tail(), n - 1)
            }

        internal class StreamIterator<A>(private var stream: Stream<A>) : Iterator<A> {

            override fun hasNext(): Boolean {
                return stream !is Empty
            }

            override fun next(): A {
                val s = stream
                return when (s) {
                    is Empty -> error("Empty stream")
                    is Cons -> s.head.also {
                        stream = s.tail()
                    }
                }
            }
        }
    }
}
