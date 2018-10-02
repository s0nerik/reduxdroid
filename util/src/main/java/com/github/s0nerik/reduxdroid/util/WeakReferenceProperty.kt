package com.github.s0nerik.reduxdroid.util

import org.jetbrains.annotations.NotNull
import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class WeakReferenceProperty<T> : ReadWriteProperty<Any, T?> {
    internal constructor()
    internal constructor(value: T) {
        reference = WeakReference(value)
    }

    private var reference: WeakReference<T?> = WeakReference(null)

    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return reference.get()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, @NotNull value: T?) {
        reference = WeakReference(value)
    }
}

fun <T> weak() = WeakReferenceProperty<T>()
fun <T> weak(value: T) = WeakReferenceProperty(value)