package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.*
import com.github.s0nerik.reduxdroid.core.state.AppState
import kotlin.reflect.jvm.jvmName

internal class Module : AppModule({
    single {
        val initialStates = initialStateProviders.mapKeys { it.key.jvmName }.mapValues { it.value(this) }
        StateStore(AppState(initialStates))
    }

    single { actionDispatcher(get(), withActionConverter = true, applyAppMiddlewares = true) }
})