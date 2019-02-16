package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.state.AppState
import com.github.s0nerik.reduxdroid.util.ReduxConfig
import org.koin.core.module.Module
import kotlin.reflect.KClass

@PublishedApi
internal const val ACTION_CONVERTERS = "ACTION_CONVERTERS"

typealias ActionConverter<A> = (action: A, state: AppState, dispatch: ActionDispatcher) -> Unit

@PublishedApi
internal data class ActionConverterHolder<A>(
        val converter: ActionConverter<A>
)

internal val actionConverters
    get() = ReduxConfig.getNonUniqueKeyMap<KClass<*>, ActionConverterHolder<Any>>(ACTION_CONVERTERS)

inline fun <reified A : Any> Module.actionConverter(
        noinline converter: ActionConverter<A>
) = ReduxConfig.addNonUniqueKeyMapEntry(
        configKey = ACTION_CONVERTERS,
        key = A::class,
        value = ActionConverterHolder(converter) as ActionConverterHolder<Any>
)

fun <A : Any> Module.actionConverter(
        actionClass: KClass<A>,
        converter: ActionConverter<A>
) = ReduxConfig.addNonUniqueKeyMapEntry(
        configKey = ACTION_CONVERTERS,
        key = actionClass,
        value = ActionConverterHolder(converter) as ActionConverterHolder<Any>
)