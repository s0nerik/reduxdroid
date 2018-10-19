package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.*
import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.Dispatcher
import kotlin.reflect.jvm.jvmName

internal class Module : AppModule({
    single { StateStore(AppState(initialStates().mapKeys { it.key.jvmName })) }

    single {
        val middlewares = listOf(ActionConverterMiddleware(actionConverters)) + appMiddlewares

        val dispatcher = Dispatcher.forStore(get<StateStore>(), get<ActionReducer>())
                .chain(middlewares.reversed())

        ActionDispatcherImpl(dispatcher) as ActionDispatcher
    }
})