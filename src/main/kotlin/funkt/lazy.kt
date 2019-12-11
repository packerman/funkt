package funkt

class Lazy<out A>(function: () -> A) : () -> A {

    private val value: A by lazy(function)

    override fun invoke(): A = value
}
