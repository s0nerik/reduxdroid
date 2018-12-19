package com.github.s0nerik.reduxdroid.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.github.s0nerik.reduxdroid.core.state.AppState

@PublishedApi
internal fun <T> LiveData<AppState>.distinctMediatorLiveData(distinct: Boolean, default: T? = null, valueSelector: (AppState) -> T): LiveData<T> {
    val mediator: MediatorLiveData<T> = MediatorLiveData()
    var latestValue: T? = default
    mediator.addSource(this) {
        val newValue = valueSelector(it)
        if (!distinct || latestValue != newValue) {
            mediator.value = newValue
            latestValue = newValue
        }
    }
    return mediator
}

@MainThread
inline fun <reified T : Any> LiveData<AppState>.get(distinct: Boolean = true): LiveData<T> =
        distinctMediatorLiveData(distinct) { it.get<T>() }

@MainThread
inline fun <reified S : Any, T : Any> LiveData<AppState>.get(
        crossinline selector: (S) -> T,
        distinct: Boolean = true
): LiveData<T> = distinctMediatorLiveData(distinct) { it.get(selector) }

@MainThread
inline fun <reified S : Any, T : Any> LiveData<AppState>.get(
        crossinline selector: (S) -> T?,
        default: T,
        distinct: Boolean = true
): LiveData<T> = distinctMediatorLiveData(distinct, default) { it.get(selector) ?: default }

@MainThread
inline fun <reified S : Any, T : Any> LiveData<AppState>.getNullable(
        crossinline selector: (S) -> T?,
        distinct: Boolean = true
): LiveData<T?> = distinctMediatorLiveData(distinct) { it.get(selector) }