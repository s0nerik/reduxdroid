package com.github.s0nerik.reduxdroid.core.middleware

import kotlin.reflect.KClass
import kotlin.reflect.full.safeCast

abstract class TypedMiddleware<T : Any>(
        private val clazz: KClass<T>
) : Middleware<Any, Any> {
    abstract fun run(next: (Any) -> Any, action: T): Any

    override fun dispatch(next: (Any) -> Any, action: Any): Any =
            clazz.safeCast(action)?.let { run(next, it) } ?: next(action)
}

inline fun <reified A : Any> typedMiddleware(crossinline middlewareFun: MiddlewareFun<A, Any>): Middleware<Any, Any> =
        object : TypedMiddleware<A>(A::class) {
            override fun run(next: (Any) -> Any, action: A): Any = middlewareFun(next, action)
        }