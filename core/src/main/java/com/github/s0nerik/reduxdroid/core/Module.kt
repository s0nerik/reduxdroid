package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.*
import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.Dispatcher
import kotlin.reflect.jvm.jvmName

private val REDUXDROID_CORE_MODULE = "__reduxdroid_core__"

internal val NON_CONVERTED_DISPATCHER = "$REDUXDROID_CORE_MODULE.ActionDispatcherImpl"

internal class Module : AppModule({
    single { StateStore(AppState(initialStates().mapKeys { it.key.jvmName })) }

    single {
        val middlewares = listOf(ActionConverterMiddleware(actionConverters, nonUniqueActionConverters)) + appMiddlewares

        val dispatcher = Dispatcher.forStore(get<StateStore>(), get<ActionReducer>())
                .chain(middlewares.reversed())

        ActionDispatcherImpl(dispatcher) as ActionDispatcher
    }

    module(REDUXDROID_CORE_MODULE) {
        single {
            val dispatcher = Dispatcher.forStore(get<StateStore>(), get<ActionReducer>())
                    .chain(appMiddlewares.reversed())

            ActionDispatcherImpl(dispatcher)
        }
    }
})