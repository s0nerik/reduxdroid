package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.SimpleStore

class StateStore(initialState: AppState) {
    internal val store = SimpleStore<AppState>(initialState)

    private val realListeners = mutableMapOf<(AppState) -> Unit, StoreListener>()

    var state: AppState
        get() = store.state
        set(newState) {
            store.state = newState
        }

    fun addListener(listener: (AppState) -> Unit) {
        if (realListeners[listener] != null)
            return

        val wrappedListener = StoreListener(listener)
        store.addListener(wrappedListener)
    }

    fun removeListener(listener: (AppState) -> Unit) {
        realListeners[listener]?.let {
            store.removeListener(it)
        }
    }
}

private class StoreListener(
        private val listener: (AppState) -> Unit
) : SimpleStore.Listener<AppState> {
    override fun onNewState(state: AppState) {
        listener(state)
    }
}