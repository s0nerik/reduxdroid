package com.github.s0nerik.reduxdroid.core.state

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

@Serializable
data class AppState @PublishedApi internal constructor(
        @PublishedApi internal val state: Map<String, Any>
) {
    @Suppress("UNCHECKED_CAST")
    fun <S : Any> get(clazz: KClass<S>): S =
            if (clazz == AppState::class) {
                this as S
            } else {
                state[clazz.jvmName] as S
            }

    inline fun <reified S : Any> get(): S = get(S::class)

    inline fun <reified S : Any, T> get(selector: (S) -> T): T = selector(get())

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