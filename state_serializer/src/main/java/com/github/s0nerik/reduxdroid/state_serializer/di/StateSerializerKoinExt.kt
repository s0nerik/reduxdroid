package com.github.s0nerik.reduxdroid.state_serializer.di

import com.github.s0nerik.reduxdroid.util.ReduxConfig
import kotlinx.serialization.KSerializer
import org.koin.core.module.Module
import kotlin.reflect.KClass

@PublishedApi
internal const val STATE_SERIALIZERS = "STATE_SERIALIZERS"

internal val stateSerializers
    get() = ReduxConfig.getUniqueKeyMap<KClass<Any>, KSerializer<Any>>(STATE_SERIALIZERS)

inline fun <reified T : Any, S : KSerializer<T>> Module.stateSerializer(serializer: S) =
        ReduxConfig.addUniqueKeyMapEntry(
                configKey = STATE_SERIALIZERS,
                key = T::class,
                value = serializer,
                duplicateKeyError = "Duplicate serializer for ${T::class} is provided"
        )