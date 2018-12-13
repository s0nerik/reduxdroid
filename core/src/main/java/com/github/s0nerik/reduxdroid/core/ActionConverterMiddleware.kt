package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.ActionConverter
import com.github.s0nerik.reduxdroid.core.di.ActionConverterHolder
import com.github.s0nerik.reduxdroid.core.middleware.Middleware
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import kotlin.reflect.KClass

private data class ConvertersHolder(
        val converters: List<ActionConverter<Any, Any>>
)

internal class ActionConverterMiddleware(
        converters: Map<KClass<*>, List<ActionConverterHolder<Any, Any>>>
) : Middleware<Any, Any>, KoinComponent {
    private val dispatcher: ActionDispatcher by inject()

    private val converterHolders: Map<KClass<*>, ConvertersHolder> = converters.mapValues {
        ConvertersHolder(
                converters = it.value.map { it.converter }
        )
    }

    override fun dispatch(next: (Any) -> Any, action: Any): Any {
        val resultAction = next(action)

        converterHolders[action::class]?.let { holder ->
            holder.converters.forEach { converter ->
                converter(action)?.let {
                    dispatcher.dispatch(it)
                }
            }
        }

        return resultAction
    }
}