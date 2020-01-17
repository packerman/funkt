package funkt

sealed class List<out A> : Iterable<A> {

    abstract fun isEmpty(): Boolean

    abstract val length: Int

    fun tail(): List<A> = when (this) {
        is Nil -> error("Empty list")
        is Cons -> tail
    }

    fun setHead(head: @UnsafeVariance A): List<A> = when (this) {
        is Nil -> error("Empty list")
        is Cons -> Cons(head, tail)
    }

    fun find(p: (A) -> Boolean): Option<A> = when (this) {
        is Nil -> Option()
        is Cons -> if (p(head)) Option(head) else tail.find(p)
    }

    fun cons(a: @UnsafeVariance A): List<A> = Cons(a, this)

    fun unCons(): Option<Pair<A, List<A>>> = when (this) {
        is Nil -> Option()
        is Cons -> Option.some(head to tail)
    }

    fun <B> foldLeft(identity: B, f: (B, A) -> B): B {
        tailrec fun iter(list: List<A>, acc: B): B = when (list) {
            is Nil -> acc
            is Cons -> iter(list.tail, f(acc, list.head))
        }
        return iter(this, identity)
    }

    override fun iterator(): Iterator<A> = ListIterator(this)

    fun concat(other: List<@UnsafeVariance A>): List<A> = concat(this, other)

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

        override val length: Int = 0
    }

    internal data class Cons<A>(internal val head: A, internal val tail: List<A>) : List<A>() {

        override fun isEmpty(): Boolean = false

        override val length: Int = tail.length + 1

        override fun toString(): String = super.toString()
    }

    companion object {

        operator fun <A> invoke(vararg elements: A): List<A> =
            elements.foldRight(Nil) { e: A, l: List<A> -> Cons(e, l) }

        fun <A> concat(list1: List<A>, list2: List<A>): List<A> = list1.foldRight(list2) { a, l -> l.cons(a) }

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

fun <A> List<A>.toStream(): Stream<A> = unCons().map { (head, tail) ->
    Stream.cons(head) { tail.toStream() }
}.getOrElse { Stream() }

fun <A> List<A>.reverse(): List<A> = foldLeft(List()) { r, a -> r.cons(a) }

fun <A, B> List<A>.foldRight(identity: B, f: (A, B) -> B): B = reverse().foldLeft(identity) { b, a -> f(a, b) }

fun <A, B> List<A>.map(f: (A) -> B): List<B> = foldRight(List()) { a, l -> l.cons(f(a)) }

fun <A> List<A>.filter(predicate: (A) -> Boolean): List<A> =
    foldRight(List()) { a, l -> if (predicate(a)) l.cons(a) else l }

fun <A> List<A>.remove(predicate: (A) -> Boolean): List<A> =
    foldRight(List()) { a, l -> if (!predicate(a)) l.cons(a) else l }

typealias Assoc<A, B> = List<Pair<A, B>>

fun <A, B> Assoc<A, B>.assoc(a: A, b: B): Assoc<A, B> = cons(a to b)

fun <A, B> Assoc<A, B>.lookup(a: A): Option<B> = find { it.first == a }.map { it.second }
