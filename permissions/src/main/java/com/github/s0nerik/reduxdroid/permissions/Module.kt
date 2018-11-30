package com.github.s0nerik.reduxdroid.permissions

import com.github.s0nerik.reduxdroid.core.di.AppModule

internal class Module : AppModule({
    single { PermissionsMiddlewareImpl() as PermissionsMiddleware }
})
