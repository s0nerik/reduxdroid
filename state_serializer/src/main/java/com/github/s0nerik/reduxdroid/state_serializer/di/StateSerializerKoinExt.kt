package com.github.s0nerik.reduxdroid.state_serializer.di

import kotlinx.serialization.KSerializer
import org.koin.core.definition.DefinitionContext
import kotlin.reflect.KClass

@PublishedApi
internal val STATE_SERIALIZERS_KEY = "_stateSerializers"

inline fun <reified T : Any, S : KSerializer<T>> DefinitionContext.stateSerializer(serializer: S) {
    val items = koin.getProperty<MutableMap<KClass<*>, KSerializer<*>>>(STATE_SERIALIZERS_KEY) ?: mutableMapOf()
    items[T::class] = serializer
    koin.setProperty(STATE_SERIALIZERS_KEY, items)
}

internal fun DefinitionContext.stateSerializers(): Map<KClass<Any>, KSerializer<Any>> =
        koin.getProperty(STATE_SERIALIZERS_KEY) ?: mapOf()