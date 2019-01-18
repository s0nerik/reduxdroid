package com.github.s0nerik.reduxdroid.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.StateStore
import com.github.s0nerik.reduxdroid.core.Thunk
import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.android.lifecycle.LiveDataAdapter

abstract class ReduxViewModel(
        private val store: StateStore,
        private val dispatcher: ActionDispatcher
) : ViewModel(), ActionDispatcher by dispatcher {

    @Suppress("Annotator")
    @get:MainThread
    protected val state: LiveData<AppState> = LiveDataAdapter.liveData(store.store)

    @get:MainThread
    protected val currentState: AppState
        get() = store.state

    @MainThread
    fun <T> LiveData<T>.mutable(actionProvider: (T) -> Any): MutableLiveData<T> {
        var latestValue: T? = null
        val result = object : MediatorLiveData<T>() {
            override fun setValue(value: T) {
                if (value != latestValue)
                    dispatch(actionProvider(value))
            }

            fun doSetValue(value: T) {
                super.setValue(value)
                latestValue = value
            }
        }
        result.addSource(this) { result.doSetValue(it) }
        return result
    }

    @MainThread
    fun <T> LiveData<T>.mutableThunk(thunkProvider: (T) -> Thunk): MutableLiveData<T> {
        var latestValue: T? = null
        val result = object : MediatorLiveData<T>() {
            override fun setValue(value: T) {
                if (value != latestValue)
                    dispatch(thunkProvider(value))
            }

            fun doSetValue(value: T) {
                super.setValue(value)
                latestValue = value
            }
        }
        result.addSource(this) { result.doSetValue(it) }
        return result
    }
}