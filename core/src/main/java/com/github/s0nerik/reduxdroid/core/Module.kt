package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.*
import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.Dispatcher
import kotlin.reflect.jvm.jvmName

internal val NON_CONVERTED_DISPATCHER = "__nonConvertedDispatcher__"

internal class Module : AppModule({
    single { StateStore(AppState(initialStates().mapKeys { it.key.jvmName })) }

    single {
        val middlewares = listOf(ActionConverterMiddleware(actionConverters, filteredActionConverters)) + appMiddlewares

        val dispatcher = Dispatcher.forStore(get<StateStore>(), get<ActionReducer>())
                .chain(middlewares.reversed())

        ActionDispatcherImpl(dispatcher)
    } bind ActionDispatcher::class

    single(NON_CONVERTED_DISPATCHER) {
        val dispatcher = Dispatcher.forStore(get<StateStore>(), get<ActionReducer>())
                .chain(appMiddlewares.reversed())

        ActionDispatcherImpl(dispatcher)
    } bind ActionDispatcher::class
})