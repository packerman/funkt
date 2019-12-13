package funkt

sealed class List<out A> {

    abstract fun isEmpty(): Boolean

    fun find(p: (A) -> Boolean): Option<A> = when (this) {
        is Nil -> Option()
        is Cons -> if (p(head)) Option(head) else tail.find(p)
    }

    fun cons(a: @UnsafeVariance A): List<A> = Cons(a, this)

    internal object Nil : List<Nothing>() {

        override fun isEmpty(): Boolean = true
    }

    internal class Cons<A>(val head: A, val tail: List<A>) : List<A>() {

        override fun isEmpty(): Boolean = false
    }

    companion object {

        operator fun <A> invoke(vararg elements: A): List<A> =
            elements.foldRight(Nil) { e: A, l: List<A> -> Cons(e, l) }
    }
}

typealias Assoc<A, B> = List<Pair<A, B>>

fun <A, B> Assoc<A, B>.assoc(a: A, b: B): Assoc<A, B> = cons(a to b)

fun <A, B> Assoc<A, B>.lookup(a: A): Option<B> = find { it.first == a }.map { it.second }

fun <A, B> emptyAssoc(): Assoc<A, B> = List()

fun <A, B> assocPairs(vararg pairs: Pair<A, B>): Assoc<A, B> = List(*pairs)
