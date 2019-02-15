package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.ReduxConfig
import com.github.s0nerik.reduxdroid.core.ReduxConfig.addNonUniqueKeyMapEntry
import com.github.s0nerik.reduxdroid.core.state.AppState
import org.koin.core.definition.DefinitionContext
import org.koin.core.module.Module
import kotlin.reflect.KClass

typealias ActionConverter<A> = (action: A, state: AppState, dispatch: ActionDispatcher) -> Unit

@PublishedApi
internal data class ActionConverterHolder<A>(
        val converter: ActionConverter<A>
)

inline fun <reified A : Any> Module.actionConverter(
        noinline converter: ActionConverter<A>
) = ReduxConfig.addNonUniqueKeyMapEntry(
        map = ReduxConfig.actionConverters,
        key = A::class,
        value = ActionConverterHolder(converter) as ActionConverterHolder<Any>
)