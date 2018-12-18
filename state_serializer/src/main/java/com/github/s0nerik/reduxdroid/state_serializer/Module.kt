package com.github.s0nerik.reduxdroid.state_serializer

import com.github.s0nerik.reduxdroid.core.di.AppModule
import com.github.s0nerik.reduxdroid.state_serializer.di.stateSerializers

internal class Module : AppModule({
    single { AppStateSerializerImpl(get(), get(), stateSerializers()) as AppStateSerializer }
})
