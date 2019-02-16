package com.github.s0nerik.reduxdroid.util

object ReduxConfig {
    private val configs = mutableMapOf<String, Any>()

    fun clear() = configs.clear()

    fun <T> get(key: String): T? = configs[key] as? T

    fun setOnce(configKey: String, value: Any, duplicateError: String) {
        if (get<Any>(configKey) != null) {
            error(duplicateError)
        }
        configs[configKey] = value
    }

    fun <K, V> addUniqueKeyMapEntry(configKey: String, key: K, value: V, duplicateKeyError: String) {
        val map = get(configKey) ?: mutableMapOf<K, V>()

        if (map.containsKey(key))
            error(duplicateKeyError)

        map[key] = value
    }

    fun <K, V> addNonUniqueKeyMapEntry(configKey: String, key: K, value: V) {
        val map = get(configKey) ?: mutableMapOf<K, MutableList<V>>()
        map.getOrPut(key) { mutableListOf() } += value
    }

    fun <K, V> getUniqueKeyMap(configKey: String) = get<Map<K, V>>(configKey) ?: emptyMap()
    fun <K, V> getNonUniqueKeyMap(configKey: String) = get<Map<K, List<V>>>(configKey) ?: emptyMap()
}