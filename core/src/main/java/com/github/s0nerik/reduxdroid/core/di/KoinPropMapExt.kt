package com.github.s0nerik.reduxdroid.core.di

import org.koin.dsl.context.ModuleDefinition

@PublishedApi
internal fun <K, V> ModuleDefinition.addUniqueKeyMapEntry(
        propertyName: String,
        itemKey: K,
        itemValue: V,
        duplicateKeyError: String
) {
    val items = koinContext.getProperty<MutableMap<K, V>>(propertyName, mutableMapOf())
    if (items.containsKey(itemKey))
        error(duplicateKeyError)

    items[itemKey] = itemValue
    koinContext.setProperty(propertyName, items)
}

@PublishedApi
internal fun <K, V> ModuleDefinition.addNonUniqueKeyMapEntry(
        propertyName: String,
        itemKey: K,
        itemValue: V
) {
    val items = koinContext.getProperty<MutableMap<K, MutableList<V>>>(propertyName, mutableMapOf())
    items.getOrPut(itemKey) { mutableListOf() } += itemValue
    koinContext.setProperty(propertyName, items)
}

@PublishedApi
internal fun <K, V> ModuleDefinition.getUniqueKeyMap(propertyName: String) =
        koinContext.getProperty<Map<K, V>>(propertyName, emptyMap())

@PublishedApi
internal fun <K, V> ModuleDefinition.getNonUniqueKeyMap(propertyName: String) =
        koinContext.getProperty<Map<K, List<V>>>(propertyName, emptyMap())