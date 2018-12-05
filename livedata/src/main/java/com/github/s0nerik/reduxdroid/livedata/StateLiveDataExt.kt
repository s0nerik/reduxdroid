package com.github.s0nerik.reduxdroid.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.github.s0nerik.reduxdroid.core.state.AppState

@MainThread
inline fun <reified S : Any> LiveData<AppState>.get() =
        Transformations.map(this) { it.get<S>() } as LiveData<S>

@MainThread
inline fun <reified S : Any, T : Any> LiveData<AppState>.get(crossinline selector: (S) -> T) =
        Transformations.map(this) { it.get(selector) } as LiveData<T>

@MainThread
inline fun <reified S : Any, T : Any> LiveData<AppState>.get(crossinline selector: (S) -> T?, default: T) =
        Transformations.map(this) { it.get(selector) ?: default } as LiveData<T>

@MainThread
inline fun <reified S : Any, T : Any> LiveData<AppState>.getNullable(crossinline selector: (S) -> T?) =
        Transformations.map(this) { it.get(selector) } as LiveData<T?>