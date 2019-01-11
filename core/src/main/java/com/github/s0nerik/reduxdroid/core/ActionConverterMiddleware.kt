package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.ActionConverter
import com.github.s0nerik.reduxdroid.core.di.ActionConverterHolder
import com.github.s0nerik.reduxdroid.core.middleware.Middleware
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import kotlin.reflect.KClass

internal class ActionConverterMiddleware(
        converters: Map<KClass<*>, List<ActionConverterHolder<Any>>>
) : Middleware<Any, Any>, KoinComponent {
    private val dispatcher: ActionDispatcher by inject()
    private val store: StateStore by inject()

    private val converters: Map<KClass<*>, List<ActionConverter<Any>>> = converters.mapValues {
        it.value.map { it.converter }
    }

    override fun dispatch(next: (Any) -> Any, action: Any): Any {
        val resultAction = next(action)

        converters[action::class]?.forEach { converter ->
            converter(action, store.state, dispatcher)
        }

        return resultAction
    }
}