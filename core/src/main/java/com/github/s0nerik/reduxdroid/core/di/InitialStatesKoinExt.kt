package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.util.ReduxConfig
import org.koin.core.definition.DefinitionContext
import org.koin.core.module.Module
import kotlin.reflect.KClass

@PublishedApi
internal const val INITIAL_STATES = "INITIAL_STATES"

internal val initialStateProviders
    get() = ReduxConfig.getUniqueKeyMap<KClass<*>, DefinitionContext.() -> Any>(INITIAL_STATES)

inline fun <reified S : Any> Module.state(noinline state: DefinitionContext.() -> S) = ReduxConfig.addUniqueKeyMapEntry(
        configKey = INITIAL_STATES,
        key = S::class,
        value = state,
        duplicateKeyError = "Multiple initial state registrations detected. State type: ${S::class}"
)
