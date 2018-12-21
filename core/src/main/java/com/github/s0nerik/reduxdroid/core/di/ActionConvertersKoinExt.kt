package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.core.ThunkDispatcher
import com.github.s0nerik.reduxdroid.core.state.AppState
import org.koin.dsl.context.ModuleDefinition
import kotlin.reflect.KClass

@PublishedApi
internal val ACTION_CONVERTERS_KEY = "_actionConverters"

typealias ActionConverter<A> = (action: A, state: AppState, dispatch: ThunkDispatcher) -> Unit

@PublishedApi
internal data class ActionConverterHolder<A>(
        val converter: ActionConverter<A>
)

internal val ModuleDefinition.actionConverters
    get() = getNonUniqueKeyMap<KClass<*>, ActionConverterHolder<Any>>(ACTION_CONVERTERS_KEY)

inline fun <reified A> ModuleDefinition.actionConverter(
        noinline converter: ActionConverter<A>
) = addNonUniqueKeyMapEntry(
        propertyName = ACTION_CONVERTERS_KEY,
        itemKey = A::class,
        itemValue = ActionConverterHolder(converter)
)