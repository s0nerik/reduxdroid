package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.core.middleware.Middleware
import org.koin.core.context.GlobalContext
import org.koin.core.definition.DefaultContext
import org.koin.core.definition.DefinitionContext
import org.koin.core.module.Module

@PublishedApi
internal val MIDDLEWARES_KEY = "MIDDLEWARES_KEY"

/**
 * Registers a list of custom middlewares to be used in the app. In logical order.
 */
fun Module.middlewares(middlewaresProvider: () -> List<Middleware<*, *>>) {
    val koin = GlobalContext.get().koin
    if (koin.getProperty<Any?>(MIDDLEWARES_KEY) != null) {
        error("Middlewares can only be registered once. Consider placing middleware registration logic into a main application module.")
    }
    koin.setProperty(MIDDLEWARES_KEY, middlewaresProvider)
}

internal val DefinitionContext.appMiddlewares: List<Middleware<Any, Any>>
    get() = (koin.getProperty<() -> List<Middleware<Any, Any>>>(MIDDLEWARES_KEY) ?: { emptyList() })()