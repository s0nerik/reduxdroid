package com.github.s0nerik.reduxdroid.navigation

import com.github.s0nerik.reduxdroid.core.di.AppModule
import com.github.s0nerik.reduxdroid.navigation.middleware.NavigationMiddleware
import com.github.s0nerik.reduxdroid.navigation.middleware.NavigationMiddlewareImpl

internal class Module : AppModule({
    single { NavigationMiddlewareImpl(get()) as NavigationMiddleware }
})
