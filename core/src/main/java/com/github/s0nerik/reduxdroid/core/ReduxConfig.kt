package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.*
import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.Reducer
import me.tatarka.redux.Reducers
import kotlin.reflect.KClass

@PublishedApi
internal object ReduxConfig {
    val initialStates = mutableMapOf<KClass<*>, Any>()
    val actionConverters = mutableMapOf<KClass<*>, MutableList<ActionConverterHolder<Any>>>()
    val actionTypeReducers = mutableMapOf<KClass<*>, (Any, AppState) -> AppState>()
    val filteredReducers = mutableMapOf<KClass<*>, MutableList<(Any, AppState) -> AppState>>()

    @PublishedApi
    internal fun <K, V> addUniqueKeyMapEntry(map: MutableMap<K, V>, key: K, value: V, duplicateKeyError: String) {
        if (map.containsKey(key))
            error(duplicateKeyError)

        map[key] = value
    }

    @PublishedApi
    internal fun <K, V> addNonUniqueKeyMapEntry(map: MutableMap<K, MutableList<V>>, key: K, value: V) {
        map.getOrPut(key) { mutableListOf() } += value
    }
}