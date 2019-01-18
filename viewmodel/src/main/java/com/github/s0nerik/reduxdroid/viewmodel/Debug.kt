package com.github.s0nerik.reduxdroid.viewmodel

import com.github.s0nerik.reduxdroid.core.ReduxDebug
import me.tatarka.redux.android.lifecycle.LiveDataAdapter

fun ReduxDebug.setDebugLiveData(enable: Boolean) {
    LiveDataAdapter.setDebugAll(enable)
}