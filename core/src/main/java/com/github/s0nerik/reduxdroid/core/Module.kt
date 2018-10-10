package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.*
import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.Dispatcher
import kotlin.reflect.jvm.jvmName

private val REDUXDROID_CORE_MODULE = "__reduxdroid_core__"
private val NON_CONVERTED_DISPATCHER_NAME = "__nonConvertedDispatcher__"

internal val NON_CONVERTED_DISPATCHER = "$REDUXDROID_CORE_MODULE.$NON_CONVERTED_DISPATCHER_NAME"

internal class Module : AppModule({
    single { StateStore(AppState(initialStates().mapKeys { it.key.jvmName })) }

    single {
        val middlewares = listOf(ActionConverterMiddleware(actionConverters, filteredActionConverters)) + appMiddlewares

        val dispatcher = Dispatcher.forStore(get<StateStore>(), get<ActionReducer>())
                .chain(middlewares.reversed())

        ActionDispatcherImpl(dispatcher)
    } bind ActionDispatcher::class

    module(REDUXDROID_CORE_MODULE) {
        single(NON_CONVERTED_DISPATCHER_NAME) {
            val dispatcher = Dispatcher.forStore(get<StateStore>(), get<ActionReducer>())
                    .chain(appMiddlewares.reversed())

            ActionDispatcherImpl(dispatcher)
        } bind ActionDispatcher::class
    }
})