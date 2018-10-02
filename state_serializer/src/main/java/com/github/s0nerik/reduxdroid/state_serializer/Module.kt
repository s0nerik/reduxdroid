package com.github.s0nerik.reduxdroid.state_serializer

import com.github.s0nerik.reduxdroid.core.di.AppModule

internal class Module : AppModule({
    single { AppStateSerializerImpl(get(), get()) } bind AppStateSerializer::class
})
