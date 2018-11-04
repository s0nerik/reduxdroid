package com.github.s0nerik.reduxdroid.state_serializer

import com.github.s0nerik.reduxdroid.core.di.AppModule
import com.github.s0nerik.reduxdroid.state_serializer.di.stateSerializer
import com.github.s0nerik.reduxdroid.state_serializer.di.stateSerializers
import com.github.s0nerik.reduxdroid.state_serializer.serializers.UriSerializer

internal class Module : AppModule({
    single { AppStateSerializerImpl(get(), get(), stateSerializers()) as AppStateSerializer }

    stateSerializer(UriSerializer)
})
