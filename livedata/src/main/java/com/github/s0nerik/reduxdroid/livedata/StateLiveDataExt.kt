package com.github.s0nerik.reduxdroid.livedata

import androidx.lifecycle.LiveData
import com.github.s0nerik.reduxdroid.core.state.AppState
import com.shopify.livedataktx.map
import com.shopify.livedataktx.nonNull

inline fun <reified S, T : Any> LiveData<AppState>.get(noinline mapper: (S) -> T) =
        nonNull().map { state -> mapper(state.get()) }

inline fun <reified S, T : Any> LiveData<AppState>.getNonNull(noinline mapper: (S) -> T?) =
        nonNull().map { state -> mapper(state.get()) }
                .nonNull()
                .map { it!! }

inline fun <reified S, T : Any> LiveData<AppState>.getNullable(noinline mapper: (S) -> T?) =
        nonNull().map { state -> mapper(state.get()) }

inline fun <reified S, T : Any> LiveData<AppState>.getAsString(noinline mapper: (S) -> T?, nullStr: String = "") =
        nonNull().map { state -> mapper(state.get())?.toString() ?: nullStr }