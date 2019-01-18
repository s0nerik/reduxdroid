package com.github.s0nerik.reduxdroid.testing

import com.github.s0nerik.reduxdroid.core.StateStore
import com.github.s0nerik.reduxdroid.core.middleware.Middleware
import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.middleware.TestMiddleware

class TestMiddleware(store: StateStore) : Middleware<Any, Any> {
    @Suppress("Annotator")
    private val testMiddleware = TestMiddleware<AppState, Any, Any>(store.store)

    val actions: List<Any>
        get() = testMiddleware.actions()

    val states: List<AppState>
        get() = testMiddleware.states()

    override fun dispatch(next: (Any) -> Any, action: Any) = testMiddleware.dispatch(next, action)

    fun reset() = testMiddleware.reset()
}

val com.github.s0nerik.reduxdroid.testing.TestMiddleware.lastState: AppState
    get() = states.last()