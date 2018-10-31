package com.github.s0nerik.reduxdroid.state_serializer

import android.content.Context
import android.util.Log
import com.github.s0nerik.reduxdroid.core.StateStore
import com.github.s0nerik.reduxdroid.core.state.AppState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.CBOR
import kotlinx.serialization.context.SimpleModule
import kotlin.reflect.KClass

interface AppStateSerializer {
    fun save()
    fun restore()
    var debugMode: Boolean
}

internal class AppStateSerializerImpl(
        private val store: StateStore,
        private val ctx: Context,
        private val serializers: Map<KClass<Any>, KSerializer<Any>>
) : AppStateSerializer {
    private val cbor: CBOR

    override var debugMode: Boolean = false

    init {
        cbor = CBOR().apply {
            serializers.forEach {
                install(SimpleModule(it.key, it.value))
            }
        }
    }

    override fun save() {
        try {
            var start: Long = 0
            if (debugMode) {
                start = System.currentTimeMillis()
            }

            doSave()

            if (debugMode) {
                Log.d("AppStateSerializer", "State serialization took ${System.currentTimeMillis() - start} millis")
            }
        } catch (t: Throwable) {
            if (debugMode) {
                Log.e("AppStateSerializer", "State serialization error", t)
            }
        }
    }

    private fun doSave() {
        ctx.openFileOutput(APP_STATE_FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(cbor.dump(AppState.serializer(), store.state))
            it.flush()
        }
    }

    override fun restore() {
        try {
            var start: Long = 0
            if (debugMode) {
                start = System.currentTimeMillis()
            }

            doRestore()

            if (debugMode) {
                Log.d("AppStateSerializer", "State deserialization took ${System.currentTimeMillis() - start} millis")
            }
        } catch (t: Throwable) {
            if (debugMode) {
                Log.e("AppStateSerializer", "State deserialization error", t)
            }
        }
    }

    private fun doRestore() {
        ctx.openFileInput(APP_STATE_FILE_NAME).use {
            val state = cbor.load(AppState.serializer(), it.readBytes())
            store.state = state
        }
        ctx.deleteFile(APP_STATE_FILE_NAME)
    }

    companion object {
        private val APP_STATE_FILE_NAME = "_last_app_state"
    }
}