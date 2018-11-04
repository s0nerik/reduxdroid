package com.github.s0nerik.reduxdroid.activity_result

import com.github.s0nerik.reduxdroid.core.di.AppModule

internal class Module : AppModule({
    single { ActivityResultMiddlewareImpl() as ActivityResultMiddleware }
})
