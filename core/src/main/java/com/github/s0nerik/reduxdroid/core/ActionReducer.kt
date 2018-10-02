package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.Reducer

typealias ActionReducer = Reducer<Any, AppState>