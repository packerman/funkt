package funkt

import funkt.List.Cons
import funkt.List.Nil

sealed class List<out A> : Iterable<A> {

    abstract fun isEmpty(): Boolean

    abstract val length: Int

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

    fun <B> map(f: (A) -> B): List<B> = buildList(this) { a, builder ->
        builder.add(f(a))
    }

    fun filter(predicate: (A) -> Boolean): List<A> = buildList(this) { a, builder ->
        if (predicate(a)) {
            builder.add(a)
        }
    }

    fun remove(predicate: (A) -> Boolean): List<A> = buildList(this) { a, builder ->
        if (!predicate(a)) {
            builder.add(a)
        }
    }

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

    internal data class Cons<A>(internal val head: A, internal var tail: List<A>) : List<A>() {

        override fun isEmpty(): Boolean = false

        override val length: Int = tail.length + 1

        override fun toString(): String = super.toString()
    }

    companion object {

        operator fun <A> invoke(vararg elements: A): List<A> =
            elements.foldRight(Nil) { e: A, l: List<A> -> Cons(e, l) }

        fun <A> concat(list1: List<A>, list2: List<A>): List<A> = ListBuilder(list1).addAndBuild(list2)

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

        private tailrec fun <A, B> buildList(
            list: List<A>,
            builder: ListBuilder<B> = ListBuilder(),
            action: (A, ListBuilder<B>) -> Unit
        ): List<B> = when (list) {
            is Nil -> builder.build()
            is Cons -> {
                action(list.head, builder)
                buildList(list.tail, builder, action)
            }
        }
    }
}

fun <A> List<A>.toStream(): Stream<A> = unCons().map { (head, tail) ->
    Stream.cons(head) { tail.toStream() }
}.getOrElse { Stream() }

fun <A> List<A>.reverse(): List<A> = foldLeft(List()) { r, a -> r.cons(a) }

typealias Assoc<A, B> = List<Pair<A, B>>

fun <A, B> Assoc<A, B>.assoc(a: A, b: B): Assoc<A, B> = cons(a to b)

fun <A, B> Assoc<A, B>.lookup(a: A): Option<B> = find { it.first == a }.map { it.second }

internal class ListBuilder<A> {

    private var first: List<A>? = null
    private var last: Cons<A>? = null

    fun add(a: A): ListBuilder<A> {
        val new = Cons(a, Nil)
        updateRefs(new)
        last = new
        return this
    }

    fun addAll(es: Iterable<A>): ListBuilder<A> {
        for (e in es) {
            add(e)
        }
        return this
    }

    fun build(): List<A> = first?.let { it } ?: Nil

    fun addAndBuild(list: List<A>): List<A> {
        updateRefs(list)
        return build()
    }

    private fun updateRefs(list: List<A>) {
        val l = last
        if (l != null) {
            l.tail = list
        } else {
            first = list
        }
    }

    companion object {

        operator fun <A> invoke(es: Iterable<A>): ListBuilder<A> = ListBuilder<A>().addAll(es)
    }
}

internal fun <A> buildList(block: ListBuilder<A>.() -> Unit): List<A> =
    ListBuilder<A>().apply(block).build()
