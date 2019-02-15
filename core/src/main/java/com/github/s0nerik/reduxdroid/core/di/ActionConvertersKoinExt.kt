package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.state.AppState
import org.koin.dsl.context.ModuleDefinition
import kotlin.reflect.KClass

@PublishedApi
internal val ACTION_CONVERTERS_KEY = "_actionConverters"

typealias ActionConverter<A> = (action: A, state: AppState, dispatch: ActionDispatcher) -> Unit

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

fun <A : Any> ModuleDefinition.actionConverter(
        actionClass: KClass<A>,
        converter: ActionConverter<A>
) = addNonUniqueKeyMapEntry(
        propertyName = ACTION_CONVERTERS_KEY,
        itemKey = actionClass,
        itemValue = ActionConverterHolder(converter)
)