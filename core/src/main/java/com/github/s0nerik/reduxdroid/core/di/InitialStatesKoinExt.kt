package com.github.s0nerik.reduxdroid.core.di

import com.github.s0nerik.reduxdroid.core.ReduxConfig
import org.koin.core.module.Module

inline fun <reified S : Any> Module.state(state: S) = ReduxConfig.addUniqueKeyMapEntry(
        map = ReduxConfig.initialStates,
        key = S::class,
        value = state,
        duplicateKeyError = "Multiple initial state registrations detected. State type: ${S::class}"
)
