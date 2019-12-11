package funkt

sealed class Stream<out A> {

    abstract fun isEmpty(): Boolean

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

    fun concat(other: Stream<@UnsafeVariance A>): Stream<A> = concat(this, other)

    fun <B> map(f: (A) -> B): Stream<B> =
        unCons().map { (h, t) ->
            cons(f(h), t.map { it.map(f) })
        }.getOrElse(Empty)

    fun <B> flatMap(f: (A) -> Stream<B>): Stream<B> =
        unCons().map { (h, t) -> concatLazy(f(h), Lazy { t().flatMap(f) }) }.getOrElse(Empty)

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

    internal object Empty : Stream<Nothing>() {

        override fun isEmpty(): Boolean = true
    }

    internal class Cons<A>(val head: A, val tail: Lazy<Stream<A>>) : Stream<A>() {

        override fun isEmpty(): Boolean = false
    }

    companion object {

        operator fun <A> invoke(vararg elements: A): Stream<A> = elements.toStream(0, elements.size)

        fun <A> cons(head: A, tail: () -> Stream<A>): Stream<A> = Cons(head, Lazy(tail))

        fun <A> cons(head: A, tail: Lazy<Stream<A>>): Stream<A> = Cons(head, tail)

        fun <A> iterate(a: A, f: (A) -> A): Stream<A> = Cons(a, Lazy { iterate(f(a), f) })

        fun <A> repeat(a: A): Stream<A> = Cons(a, Lazy { repeat(a) })

        tailrec fun <A> drop(stream: Stream<A>, n: Int): Stream<A> =
            if (n <= 0) stream else when (stream) {
                is Empty -> Empty
                is Cons -> drop(stream.tail(), n - 1)
            }

        fun <A> concat(stream1: Stream<A>, stream2: Stream<A>): Stream<A> =
            stream1.unCons().map { (h, t) -> cons(h, { concat(t(), stream2) }) }.getOrElse(stream2)

        private fun <A> concatLazy(stream1: Stream<A>, stream2: Lazy<Stream<A>>): Stream<A> =
            stream1.unCons().map { (h, t) -> cons(h, { concatLazy(t(), stream2) }) }.getOrElse(stream2)

        private class StreamIterator<A>(private var stream: Stream<A>) : Iterator<A> {

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

        private fun <A> Array<A>.toStream(i: Int, j: Int): Stream<A> =
            if (i < j) Cons(this[i], Lazy { toStream(i + 1, j) }) else Empty
    }
}
