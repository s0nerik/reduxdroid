package com.github.s0nerik.reduxdroid.core.middleware

internal class ReduxMiddleware<A, R>(
        private val middleware: Middleware<A, R>
) : me.tatarka.redux.middleware.Middleware<A, R> {
    override fun dispatch(next: me.tatarka.redux.middleware.Middleware.Next<A, R>, action: A): R =
            middleware.dispatch(next::next, action)
}