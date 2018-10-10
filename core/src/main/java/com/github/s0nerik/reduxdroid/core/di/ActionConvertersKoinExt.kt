package com.github.s0nerik.reduxdroid.core.di

import org.koin.dsl.context.ModuleDefinition
import kotlin.reflect.KClass

@PublishedApi
internal val ACTION_CONVERTERS_KEY = "_actionConverters"

@PublishedApi
internal val FILTERED_ACTION_CONVERTERS_KEY = "_filteredActionConverters"

typealias ActionConverter<A1, A2> = (A1) -> A2?

@PublishedApi
internal data class ActionConverterHolder<A1, A2>(
        val dropOriginalAction: Boolean,
        val converter: ActionConverter<A1, A2>
)

@PublishedApi
internal inline fun <A1, A2> _filteredActionConverter(
        crossinline converter: ActionConverter<A1, A2>,
        crossinline filter: (A1) -> Boolean
): ActionConverter<A1, A2> = { action: A1 ->
    if (filter(action))
        converter(action)
    else
        null
}

internal val ModuleDefinition.actionConverters
    get() = getUniqueKeyMap<KClass<*>, ActionConverterHolder<Any, Any>>(ACTION_CONVERTERS_KEY)

internal val ModuleDefinition.filteredActionConverters
    get() = getNonUniqueKeyMap<KClass<*>, ActionConverterHolder<Any, Any>>(FILTERED_ACTION_CONVERTERS_KEY)

/**
 * Registers the action converter to be used when action [A1] gets dispatched.
 * Every time the action [A1] gets dispatched - it will be mapped to the specified action and passed downstream.
 * Registering a reducer for the same action type more than once using this method is prohibited.
 *
 * @see [ModuleDefinition.actionConverter]
 */
inline fun <reified A1> ModuleDefinition.actionConverter(
        dropOriginalAction: Boolean = true,
        noinline converter: ActionConverter<A1, Any>
) = addUniqueKeyMapEntry(
        propertyName = ACTION_CONVERTERS_KEY,
        itemKey = A1::class,
        itemValue = ActionConverterHolder(dropOriginalAction, converter),
        duplicateKeyError = "You can't attach multiple action converters for the same action type. Action type: ${A1::class}"
)

/**
 * Registers the action converter to be used when action [A1] gets dispatched.
 * Every time the action [A1] gets dispatched - it will be mapped to the specified action and passed downstream, but
 * only if [filter] returns true, otherwise original action would be dispatched.
 * Registering a reducer for the same action type more than once using this method is prohibited.
 *
 * @see [ModuleDefinition.reducer]
 */
inline fun <reified A1> ModuleDefinition.actionConverter(
        dropOriginalAction: Boolean = true,
        crossinline converter: ActionConverter<A1, Any>,
        crossinline filter: (A1) -> Boolean
) = addNonUniqueKeyMapEntry(
        propertyName = FILTERED_ACTION_CONVERTERS_KEY,
        itemKey = A1::class,
        itemValue = ActionConverterHolder(dropOriginalAction, _filteredActionConverter(converter, filter))
)