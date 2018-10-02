package com.github.s0nerik.reduxdroid.core.base

import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.StateStore
import com.github.s0nerik.reduxdroid.core.state.AppState
import com.github.s0nerik.reduxdroid.core.util.ResourceResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import me.tatarka.redux.android.lifecycle.StoreViewModel
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(
        store: StateStore,
        private val res: ResourceResolver,
        private val dispatcher: ActionDispatcher,
        uiContext: CoroutineContext = Dispatchers.Main,
        bgContext: CoroutineContext = Dispatchers.Default,
        ioContext: CoroutineContext = Dispatchers.IO
) : StoreViewModel<AppState, StateStore>(store), ResourceResolver by res, ActionDispatcher by dispatcher, CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + uiContext

    protected val uiContext = job + uiContext
    protected val bgContext = job + bgContext
    protected val ioContext = job + ioContext

    override fun onCleared() {
        job.cancel()
    }
}