package com.github.s0nerik.reduxdroid.core

import me.tatarka.redux.Dispatcher

typealias Thunk = (dispatch: ActionDispatcher) -> Any

interface ActionDispatcher {
    fun dispatch(action: Any)
    fun dispatch(thunk: Thunk)

    operator fun invoke(action: Any)
    operator fun invoke(thunk: Thunk)
}

internal class ActionDispatcherImpl(
        private val dispatcher: Dispatcher<Any, Any>
) : ActionDispatcher {
    override fun dispatch(action: Any) { dispatcher.dispatch(action) }
    override fun dispatch(thunk: Thunk) { thunk(this) }

    override fun invoke(action: Any) = dispatch(action)
    override fun invoke(thunk: Thunk) = dispatch(thunk)
}