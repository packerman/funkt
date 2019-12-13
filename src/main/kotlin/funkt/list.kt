package funkt

sealed class List<out A> : Iterable<A> {

    abstract fun isEmpty(): Boolean

    fun find(p: (A) -> Boolean): Option<A> = when (this) {
        is Nil -> Option()
        is Cons -> if (p(head)) Option(head) else tail.find(p)
    }

    fun cons(a: @UnsafeVariance A): List<A> = Cons(a, this)

    override fun iterator(): Iterator<A> = ListIterator(this)

    override fun toString(): String = buildString {
        append('[')
        forEachIndexed { i: Int, a: A ->
            if (i > 0) {
                append(", ")
            }
            append(a)
        }
        append(']')
    }

    internal object Nil : List<Nothing>() {

        override fun isEmpty(): Boolean = true
    }

    internal data class Cons<A>(val head: A, val tail: List<A>) : List<A>() {

        override fun isEmpty(): Boolean = false

        override fun toString(): String = super.toString()
    }

    companion object {

        operator fun <A> invoke(vararg elements: A): List<A> =
            elements.foldRight(Nil) { e: A, l: List<A> -> Cons(e, l) }

        private class ListIterator<A>(private var list: List<A>) : Iterator<A> {

            override fun hasNext(): Boolean = list !is Nil

            override fun next(): A = list.let { l ->
                when (l) {
                    is Nil -> error("Empty list")
                    is Cons -> {
                        list = l.tail
                        l.head
                    }
                }
            }
        }
    }
}

typealias Assoc<A, B> = List<Pair<A, B>>

fun <A, B> Assoc<A, B>.assoc(a: A, b: B): Assoc<A, B> = cons(a to b)

fun <A, B> Assoc<A, B>.lookup(a: A): Option<B> = find { it.first == a }.map { it.second }
