package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.SimpleStore

class StateStore(initialState: AppState) {
    internal val store = SimpleStore<AppState>(initialState)

    var state: AppState
        get() = store.state
        set(newState) {
            store.state = newState
        }

    fun addListener(listener: (AppState) -> Unit) {
        store.addListener(listener)
    }

    fun removeListener(listener: (AppState) -> Unit) {
        store.removeListener(listener)
    }
}