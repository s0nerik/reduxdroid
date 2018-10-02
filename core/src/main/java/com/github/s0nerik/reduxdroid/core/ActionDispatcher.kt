package com.github.s0nerik.reduxdroid.core

import me.tatarka.redux.Dispatcher

typealias ThunkDispatcher = (Any) -> Unit
typealias Thunk = (dispatch: ThunkDispatcher) -> Any

interface ActionDispatcher {
    fun dispatch(action: Any)
    fun dispatch(thunk: Thunk)
}

internal open class ActionDispatcherImpl(
        private val dispatcher: Dispatcher<Any, Any>
) : ActionDispatcher {
    override fun dispatch(action: Any) { dispatcher.dispatch(action) }
    override fun dispatch(thunk: Thunk) { thunk(this::dispatch) }
}