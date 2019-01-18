package com.github.s0nerik.reduxdroid.livedata

import android.os.Looper
import androidx.lifecycle.LiveData
import com.github.s0nerik.reduxdroid.core.ReduxDebug
import com.github.s0nerik.reduxdroid.core.StateStore
import com.github.s0nerik.reduxdroid.core.state.AppState

object LiveDataAdapter {
    internal var debugAll = false

    fun liveData(store: StateStore): LiveData<AppState> {
        return liveData(store, debugAll)
    }

    fun liveData(store: StateStore, debug: Boolean): LiveData<AppState> {
        return StateStoreLiveData(store, debug)
    }

    private class StateStoreLiveData internal constructor(
            internal val store: StateStore,
            internal val debug: Boolean
    ) : LiveData<AppState>(), (AppState) -> Unit {

        @Volatile
        internal var exception: Exception? = null

        private val isMainThread: Boolean
            get() = Thread.currentThread() === Looper.getMainLooper().thread

        init {
            setValue(store.state)
        }

        override fun onActive() {
            store.addListener(this)
        }

        override fun onInactive() {
            store.removeListener(this)
        }

        override fun setValue(value: AppState) {
            val exception = this.exception
            if (debug && exception != null) {
                try {
                    super.setValue(value)
                } catch (e: Exception) {
                    appendStacktrace(e, exception)
                    throw e
                }

                this.exception = null
            } else {
                super.setValue(value)
            }
        }

        override fun invoke(state: AppState) {
            if (isMainThread) {
                setValue(state)
            } else {
                if (debug) {
                    exception = IllegalStateException()
                }
                postValue(state)
            }
        }

        private fun appendStacktrace(e: Throwable, context: Throwable) {
            val originalStacktrace = e.stackTrace
            val additionalStacktrace = context.stackTrace
            val combinedStacktrace = arrayOfNulls<StackTraceElement>(originalStacktrace.size + additionalStacktrace.size)
            System.arraycopy(originalStacktrace, 0, combinedStacktrace, 0, originalStacktrace.size)
            System.arraycopy(additionalStacktrace, 0, combinedStacktrace, originalStacktrace.size, additionalStacktrace.size)
            e.stackTrace = combinedStacktrace
        }
    }
}

fun ReduxDebug.setDebugLiveData(enable: Boolean) {
    LiveDataAdapter.debugAll = enable
}