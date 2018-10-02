package com.github.s0nerik.reduxdroid.core

import me.tatarka.redux.middleware.Middleware
import kotlin.reflect.KClass
import kotlin.reflect.full.safeCast

abstract class TypedMiddleware<T : Any>(
        private val clazz: KClass<T>
) : Middleware<Any, Any> {
    abstract fun run(next: Middleware.Next<Any, Any>, action: T): Any

    @Suppress("UNCHECKED_CAST")
    override fun dispatch(next: Middleware.Next<Any, Any>, action: Any): Any {
        return clazz.safeCast(action)?.let { run(next, it) } ?: next.next(action)
    }
}