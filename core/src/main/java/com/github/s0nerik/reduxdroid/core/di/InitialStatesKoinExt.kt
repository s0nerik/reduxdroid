package com.github.s0nerik.reduxdroid.core.di

import org.koin.dsl.context.ModuleDefinition
import kotlin.reflect.KClass

@PublishedApi
internal val INITIAL_STATES_KEY = "_initialStates"

inline fun <reified S : Any> ModuleDefinition.state(state: S) = addUniqueKeyMapEntry(
        propertyName = INITIAL_STATES_KEY,
        itemKey = S::class,
        itemValue = state,
        duplicateKeyError = "Multiple initial state registrations detected. State type: ${S::class}"
)

internal fun ModuleDefinition.initialStates(): Map<KClass<*>, Any> = getUniqueKeyMap(INITIAL_STATES_KEY)
