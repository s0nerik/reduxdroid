package com.github.s0nerik.reduxdroid.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.StateStore
import com.github.s0nerik.reduxdroid.core.state.AppState

abstract class ReduxViewModel(
//        private val store: StateStore,
        protected val store: StateStore,
        private val dispatcher: ActionDispatcher
) : ViewModel(), ActionDispatcher by dispatcher {

//    @get:MainThread
//    protected val state: LiveData<AppState> by lazy { LiveDataAdapter.liveData(store) }

    @get:MainThread
    protected val currentState: AppState
        get() = store.state
}