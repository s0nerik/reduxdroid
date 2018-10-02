package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.ActionConverter
import me.tatarka.redux.middleware.Middleware
import kotlin.reflect.KClass

internal class ActionConverterMiddleware(
        val converters: Map<KClass<*>, ActionConverter<Any, Any>>
) : Middleware<Any, Any> {
    override fun dispatch(next: Middleware.Next<Any, Any>, action: Any) =
            next.next(converters[action::class]?.invoke(action) ?: action)
}