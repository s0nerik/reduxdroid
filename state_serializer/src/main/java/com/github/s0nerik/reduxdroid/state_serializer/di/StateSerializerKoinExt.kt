package com.github.s0nerik.reduxdroid.state_serializer.di

import kotlinx.serialization.KSerializer
import org.koin.dsl.context.ModuleDefinition
import kotlin.reflect.KClass

@PublishedApi
internal val STATE_SERIALIZERS_KEY = "_stateSerializers"

inline fun <reified T : Any, S : KSerializer<T>> ModuleDefinition.stateSerializer(serializer: S) {
    val items = koinContext.getProperty<MutableMap<KClass<*>, KSerializer<*>>>(STATE_SERIALIZERS_KEY, mutableMapOf())
    items[T::class] = serializer
    koinContext.setProperty(STATE_SERIALIZERS_KEY, items)
}

internal fun ModuleDefinition.stateSerializers(): Map<KClass<Any>, KSerializer<Any>> =
        koinContext.getProperty(STATE_SERIALIZERS_KEY, mapOf())