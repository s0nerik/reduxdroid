package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.ActionConverterHolder
import me.tatarka.redux.middleware.Middleware
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import kotlin.reflect.KClass

internal class ActionConverterMiddleware(
        val converters: Map<KClass<*>, ActionConverterHolder<Any, Any>>,
        val filteredConverters: Map<KClass<*>, List<ActionConverterHolder<Any, Any>>>
) : Middleware<Any, Any>, KoinComponent {
    private val dispatcher: ActionDispatcher by inject(NON_CONVERTED_DISPATCHER)

    override fun dispatch(next: Middleware.Next<Any, Any>, action: Any): Any {
        val clazz = action::class

        filteredConverters[clazz]?.let { filteredConverters ->
            filteredConverters.forEach { converterHolder ->
                val convertedAction = converterHolder.converter(action)
                if (convertedAction != null)
                    return doDispatch(next, action, convertedAction, converterHolder.dropOriginalAction)
            }
        }

        converters[clazz]?.let { converterHolder ->
            val convertedAction = converterHolder.converter(action)
            if (convertedAction != null)
                return doDispatch(next, action, convertedAction, converterHolder.dropOriginalAction)
        }

        return doDispatch(next, action, action, true)
    }

    private fun doDispatch(next: Middleware.Next<Any, Any>, originalAction: Any, convertedAction: Any, dropOriginalAction: Boolean): Any {
        if (!dropOriginalAction)
            dispatcher.dispatch(originalAction)

        return next.next(convertedAction)
    }
}