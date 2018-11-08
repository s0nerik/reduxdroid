package com.github.s0nerik.reduxdroid.testing

import com.github.s0nerik.reduxdroid.core.StateStore
import com.github.s0nerik.reduxdroid.core.di.actionDispatcher
import com.github.s0nerik.reduxdroid.core.middleware.Middleware
import org.koin.dsl.context.ModuleDefinition

fun ModuleDefinition.testActionDispatcher(
        store: StateStore,
        testMiddleware: TestMiddleware,
        withActionConverter: Boolean = true,
        applyAppMiddlewares: Boolean = true,
        extraMiddlewares: List<Middleware<Any, Any>>
) = actionDispatcher(
        store = store,
        withActionConverter = withActionConverter,
        applyAppMiddlewares = applyAppMiddlewares,
        extraMiddlewares = extraMiddlewares + testMiddleware
)