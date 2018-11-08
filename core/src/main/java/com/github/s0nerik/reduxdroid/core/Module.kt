package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.*
import com.github.s0nerik.reduxdroid.core.state.AppState
import kotlin.reflect.jvm.jvmName

internal class Module : AppModule({
    single { StateStore(AppState(initialStates().mapKeys { it.key.jvmName })) }

    single { actionDispatcher(get(), withActionConverter = true, applyAppMiddlewares = true) }
})