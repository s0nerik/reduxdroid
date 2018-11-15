package com.github.s0nerik.reduxdroid.core.state

import kotlinx.serialization.Serializable
import kotlin.reflect.jvm.jvmName

@Serializable
data class AppState @PublishedApi internal constructor(
        @PublishedApi internal val state: Map<String, Any>
) {
    inline fun <reified S> get(): S =
            if (S::class == AppState::class) {
                this as S
            } else {
                state[S::class.jvmName] as S
            }

    inline fun <reified S, T> get(selector: (S) -> T): T = selector(get())

    inline fun <reified S : Any> set(s: S): AppState {
        if (s is AppState) {
            return AppState(s.state)
        } else {
            val newState = state.toMutableMap()
            newState[S::class.jvmName] = s
            return AppState(newState)
        }
    }
}