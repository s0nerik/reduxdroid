package com.github.s0nerik.reduxdroid.core.middleware

typealias MiddlewareFun<A, R> = (next: (A) -> R, action: A) -> R

interface Middleware<A, R> {
    /**
     * Called when an action is dispatched.
     *
     * @param next   Dispatch to the next middleware or actually update the state if there is none.
     * You can chose to call this anywhere to see the state before and after it has
     * changed or not at all to drop the action.
     * @param action This action that was dispatched.
     */
    fun dispatch(next: (A) -> R, action: A): R
}

inline fun <A, R> middleware(crossinline middlewareFun: MiddlewareFun<A, R>): Middleware<A, R> =
        object : Middleware<A, R> {
            override fun dispatch(next: (A) -> R, action: A): R = middlewareFun(next, action)
        }