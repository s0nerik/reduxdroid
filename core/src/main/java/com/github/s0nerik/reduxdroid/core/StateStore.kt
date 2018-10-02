package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.SimpleStore

class StateStore(initialState: AppState) : SimpleStore<AppState>(initialState)