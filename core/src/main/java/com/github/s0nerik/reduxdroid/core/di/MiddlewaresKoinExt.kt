package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.core.middleware.Middleware
import com.github.s0nerik.reduxdroid.util.ReduxConfig
import org.koin.core.definition.DefinitionContext
import org.koin.core.module.Module

@PublishedApi
internal const val MIDDLEWARES_KEY = "MIDDLEWARES_KEY"

/**
 * Registers a list of custom middlewares to be used in the app. In logical order.
 */
fun Module.middlewares(middlewaresProvider: () -> List<Middleware<*, *>>) = ReduxConfig.setOnce(
        configKey = MIDDLEWARES_KEY,
        value = middlewaresProvider,
        duplicateError = "Middlewares can only be registered once. Consider placing middleware registration logic into a main application module."
)

internal val DefinitionContext.appMiddlewaresProvider: () -> List<Middleware<Any, Any>>
    get() = ReduxConfig.get<() -> List<Middleware<Any, Any>>>(MIDDLEWARES_KEY) ?: { emptyList() }