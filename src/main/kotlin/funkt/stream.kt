package funkt

sealed class Stream<out A> {

    abstract fun isEmpty(): Boolean

    fun head(): Option<A> = when (this) {
        is Empty -> Option()
        is Cons -> Option.some(head)
    }

    fun unCons(): Option<Pair<A, Lazy<Stream<A>>>> = when (this) {
        is Empty -> Option()
        is Cons -> Option.some(head to tail)
    }

    fun take(n: Int): Stream<A> =
        if (n <= 0) Empty else when (this) {
            is Empty -> Empty
            is Cons -> Cons(head, tail.map { it.take(n - 1) })
        }

    fun drop(n: Int): Stream<A> = drop(this, n)

    fun concat(other: Stream<@UnsafeVariance A>): Stream<A> = concat(this, other)

    fun interleave(other: Stream<@UnsafeVariance A>): Stream<A> = interleave(this, other)

    fun <B> map(f: (A) -> B): Stream<B> =
        unCons().map { (h, t) ->
            cons(f(h), t.map { it.map(f) })
        }.getOrElse(Empty)

    fun filter(p: (A) -> Boolean): Stream<A> =
        unCons().map { (h, t) ->
            if (p(h)) cons(h, t.map { it.filter(p) })
            else t().filter(p)
        }.getOrElse(Empty)

    fun <B> flatMap(f: (A) -> Stream<B>): Stream<B> =
        unCons().map { (h, t) -> concatLazy(f(h), t.map { it.flatMap(f) }) }.getOrElse(Empty)

    fun <B, C> zip(other: Stream<B>, f: (A, B) -> C): Stream<C> = when (this) {
        is Empty -> Empty
        is Cons -> when (other) {
            is Empty -> Empty
            is Cons -> Cons(f(head, other.head), tail.map { it.zip(other.tail(), f) })
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

        operator fun <A> invoke(): Stream<A> = Empty

        operator fun <A> invoke(a: A): Stream<A> = Cons(a, Lazy { Empty })

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
            stream1.unCons().map { (h, t) -> cons(h, t.map { concat(it, stream2) }) }.getOrElse(stream2)

        fun <A> interleave(stream1: Stream<A>, stream2: Stream<A>): Stream<A> =
            stream1.unCons().map { (head1, tail1) ->
                cons(head1, tail1.map { interleave(stream2, it) })
            }.getOrElse(stream2)

        fun <A, B> lift(f: (A) -> B): (Stream<A>) -> Stream<B> = {
            it.map(f)
        }

        fun <A, B, C> lift2(f: (A, B) -> C): (Stream<A>, Stream<B>) -> Stream<C> = { stream1, stream2 ->
            stream1.flatMap { a -> stream2.map { b -> f(a, b) } }
        }

        fun <A, B, C, D> lift3(f: (A, B, C) -> D): (Stream<A>, Stream<B>, Stream<C>) -> Stream<D> =
            { stream1, stream2, stream3 ->
                stream1.flatMap { a -> stream2.flatMap { b -> stream3.map { c -> f(a, b, c) } } }
            }

        private fun <A> concatLazy(stream1: Stream<A>, stream2: Lazy<Stream<A>>): Stream<A> =
            stream1.unCons().map { (h, t) -> cons(h, t.map { concatLazy(it, stream2) }) }.getOrElse(stream2)

        private class StreamIterator<A>(private var stream: Stream<A>) : Iterator<A> {

            override fun hasNext(): Boolean = stream !is Empty

            override fun next(): A = stream.let { s ->
                when (s) {
                    is Empty -> error("Empty stream")
                    is Cons -> {
                        stream = s.tail()
                        s.head
                    }
                }
            }
        }

        private fun <A> Array<A>.toStream(i: Int, j: Int): Stream<A> =
            if (i < j) Cons(this[i], Lazy { toStream(i + 1, j) }) else Empty
    }
}
