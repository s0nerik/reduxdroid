package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.core.state.AppState
import com.github.s0nerik.reduxdroid.util.ReduxConfig
import me.tatarka.redux.Reducer
import me.tatarka.redux.Reducers
import org.koin.core.definition.DefinitionContext
import org.koin.core.module.Module
import kotlin.reflect.KClass

@PublishedApi
internal const val ACTION_TYPE_REDUCERS = "ACTION_TYPE_REDUCERS"

@PublishedApi
internal const val FILTERED_REDUCERS = "FILTERED_REDUCERS"

@PublishedApi
internal inline fun <reified A : Any, reified S : Any> _appStateReducer(
        crossinline reducer: (A, S) -> S
): (A, AppState) -> AppState = { action: A, state: AppState ->
    state.set(reducer(action, state.get()))
}

@PublishedApi
internal inline fun <reified A : Any, reified S : Any> _filteredReducer(
        crossinline filter: (A) -> Boolean,
        crossinline reducer: (A, S) -> S
): (A, AppState) -> AppState = { action: A, state: AppState ->
    if (filter(action))
        _appStateReducer(reducer)(action, state)
    else
        state
}

/**
 * Registers the reducer to be invoked when action [A] gets dispatched and [filter] returns true.
 */
inline fun <reified A : Any, reified S : Any> Module.reducer(
        crossinline reducer: (A, S) -> S,
        crossinline filter: (A) -> Boolean
) = ReduxConfig.addNonUniqueKeyMapEntry(
        configKey = FILTERED_REDUCERS,
        key = A::class,
        value = _filteredReducer(filter, reducer) as (Any, AppState) -> AppState
)

/**
 * Registers the reducer to be invoked when action [A] gets dispatched.
 * Registering a reducer for the same action type more than once using this method is prohibited.
 *
 * @see [DefinitionContext.reducer]
 */
inline fun <reified A : Any, reified S : Any> Module.reducer(
        crossinline reducer: (A, S) -> S
) = ReduxConfig.addUniqueKeyMapEntry(
        configKey = ACTION_TYPE_REDUCERS,
        key = A::class,
        value = _appStateReducer(reducer) as (Any, AppState) -> AppState,
        duplicateKeyError = "You can't attach multiple action type bound reducers for the same action. Action type: ${A::class}"
)

internal fun DefinitionContext._combinedReducer(): Reducer<Any, AppState> {
    val actionTypeReducers = ReduxConfig.getUniqueKeyMap<KClass<*>, (Any, AppState) -> AppState>(ACTION_TYPE_REDUCERS)

    var combinedActionTypeReducer = Reducers.matchClass<Any, AppState>()
    actionTypeReducers.forEach { entry ->
        combinedActionTypeReducer = combinedActionTypeReducer.`when`(entry.key.java, entry.value)
    }

    val filteredActionReducers = ReduxConfig.getNonUniqueKeyMap<KClass<*>, (Any, AppState) -> AppState>(FILTERED_REDUCERS)

    var combinedFilteredActionReducer = Reducers.matchClass<Any, AppState>()
    filteredActionReducers.forEach { entry ->
        val actionReducers = entry.value.map { Reducer(it) }.toTypedArray()
        combinedFilteredActionReducer = combinedFilteredActionReducer.`when`(entry.key.java, Reducers.all(*actionReducers))
    }

    return Reducers.all(combinedActionTypeReducer, combinedFilteredActionReducer)
}