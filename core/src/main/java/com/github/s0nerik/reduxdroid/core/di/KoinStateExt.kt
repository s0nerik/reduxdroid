package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.core.StateStore
import org.koin.dsl.context.ModuleDefinition

inline fun <reified T : Any> ModuleDefinition.state(): T {
    val store = get<StateStore>()
    return store.state.get()
}

inline fun <reified T : Any, R> ModuleDefinition.state(selector: (T) -> R): R {
    val store = get<StateStore>()
    return store.state.get(selector)
}