package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.*
import com.github.s0nerik.reduxdroid.core.state.AppState
import com.github.s0nerik.reduxdroid.core.util.ContextHolder
import com.github.s0nerik.reduxdroid.core.util.MainContextHolder
import com.github.s0nerik.reduxdroid.core.util.ResourceResolver
import com.github.s0nerik.reduxdroid.core.util.ResourceResolverImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.android.Main
import me.tatarka.redux.Dispatcher
import me.tatarka.redux.android.LogMiddleware
import kotlin.reflect.jvm.jvmName

internal class Module : AppModule({
    single { StateStore(AppState(initialStates().mapKeys { it.key.jvmName })) }

    single {
        val middlewares = listOf(
                LogMiddleware<Any, Any>("INITIAL ACTION"),
                ActionConverterMiddleware(actionConverters())
        ) + appMiddlewares + listOf(
                LogMiddleware<Any, Any>("FINAL ACTION")
        )

        val dispatcher = Dispatcher.forStore(get<StateStore>(), get<ActionReducer>())
                .chain(middlewares.reversed())

        ActionDispatcherImpl(dispatcher)
    } bind ActionDispatcher::class

    single { ResourceResolverImpl(get()) } bind ResourceResolver::class

    single {
        MainContextHolder(main = Dispatchers.Main, io = Dispatchers.IO, bg = Dispatchers.Default)
    } bind ContextHolder::class
})