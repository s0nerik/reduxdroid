package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.Reducer
import me.tatarka.redux.Reducers
import org.koin.dsl.context.ModuleDefinition
import kotlin.reflect.KClass

@PublishedApi
internal val ACTION_TYPE_REDUCERS_KEY = "_actionTypeReducers"

@PublishedApi
internal val FILTERED_ACTION_REDUCERS_KEY = "_filteredActionReducers"

@PublishedApi
internal inline fun <reified A, reified S : Any> _appStateReducer(
        crossinline reducer: (A, S) -> S
) : (A, AppState) -> AppState = { action: A, state: AppState ->
    state.set(reducer(action, state.get()))
}

@PublishedApi
internal inline fun <reified A, reified S : Any> _filteredReducer(
        crossinline filter: (A) -> Boolean,
        crossinline reducer: (A, S) -> S
) : (A, AppState) -> AppState = { action: A, state: AppState ->
    if (filter(action))
        _appStateReducer(reducer)(action, state)
    else
        state
}

/**
 * Registers the reducer to be invoked when action [A] gets dispatched and [filter] returns true.
 */
inline fun <reified A, reified S : Any> ModuleDefinition.reducer(
        crossinline reducer: (A, S) -> S,
        crossinline filter: (A) -> Boolean
) = addNonUniqueKeyMapEntry(
        propertyName = FILTERED_ACTION_REDUCERS_KEY,
        itemKey = A::class,
        itemValue = _filteredReducer(filter, reducer)
)

/**
 * Registers the reducer to be invoked when action [A] gets dispatched.
 * Registering a reducer for the same action type more than once using this method is prohibited.
 *
 * @see [ModuleDefinition.reducer]
 */
inline fun <reified A, reified S : Any> ModuleDefinition.reducer(
        crossinline reducer: (A, S) -> S
) = addUniqueKeyMapEntry(
        propertyName = ACTION_TYPE_REDUCERS_KEY,
        itemKey = A::class,
        itemValue = _appStateReducer(reducer),
        duplicateKeyError = "You can't attach multiple action type bound reducers for the same action. Action type: ${A::class}"
)

internal fun ModuleDefinition._combinedReducer(): Reducer<Any, AppState> {
    val actionTypeReducers = getUniqueKeyMap<KClass<*>, (Any, AppState) -> AppState>(ACTION_TYPE_REDUCERS_KEY)

    var combinedActionTypeReducer = Reducers.matchClass<Any, AppState>()
    actionTypeReducers.forEach { entry ->
        combinedActionTypeReducer = combinedActionTypeReducer.`when`(entry.key.java, entry.value)
    }

    val filteredActionReducers = getNonUniqueKeyMap<KClass<*>, (Any, AppState) -> AppState>(FILTERED_ACTION_REDUCERS_KEY)

    var combinedFilteredActionReducer = Reducers.matchClass<Any, AppState>()
    filteredActionReducers.forEach { entry ->
        val actionReducers = entry.value.map { Reducer(it) }.toTypedArray()
        combinedFilteredActionReducer = combinedFilteredActionReducer.`when`(entry.key.java, Reducers.all(*actionReducers))
    }

    return Reducers.all(combinedActionTypeReducer, combinedFilteredActionReducer)
}