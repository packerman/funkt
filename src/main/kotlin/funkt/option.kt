package funkt

sealed class Option<out A> {

    abstract fun isEmpty(): Boolean

    fun getOrElse(default: @UnsafeVariance A): A = when (this) {
        is None -> default
        is Some -> value
    }

    fun getOrElse(default: () -> @UnsafeVariance A): A = when (this) {
        is None -> default()
        is Some -> value
    }

    fun <B> map(f: (A) -> B): Option<B> = when (this) {
        is None -> None
        is Some -> Some(f(value))
    }

    fun <B> flatMap(f: (A) -> Option<B>): Option<B> = map(f).getOrElse(None)

    fun forEach(action: (A) -> Unit) {
        if (this is Some) action(value)
    }

    fun orElse(default: () -> Option<@UnsafeVariance A>): Option<A> = map { _ -> this }.getOrElse(default)

    internal object None : Option<Nothing>() {

        override fun isEmpty(): Boolean = true
    }

    internal data class Some<A>(val value: A) : Option<A>() {

        override fun isEmpty(): Boolean = false
    }

    companion object {

        operator fun <A> invoke(a: A?): Option<A> = when (a) {
            null -> None
            else -> Some(a)
        }

        operator fun <A> invoke(): Option<A> = None
    }
}
