package com.github.s0nerik.reduxdroid.testing

import com.github.s0nerik.reduxdroid.core.StateStore
import com.github.s0nerik.reduxdroid.core.middleware.Middleware
import com.github.s0nerik.reduxdroid.core.state.AppState

class TestMiddleware(private val store: StateStore) : Middleware<Any, Any> {
    private val _actions = mutableListOf<Any>()
    private val _states = mutableListOf<AppState>()

    val actions: List<Any> get() = _actions
    val states: List<AppState> get() = _states

    init {
        _states += store.state
    }

    override fun dispatch(next: (Any) -> Any, action: Any): Any {
        _actions += action
        val result = next(action)
        _states += store.state
        return result
    }

    fun reset() = {
        _actions.clear()

        val lastState = _states.last()
        _states.clear()
        _states += lastState
    }
}

val TestMiddleware.lastState: AppState
    get() = states.last()