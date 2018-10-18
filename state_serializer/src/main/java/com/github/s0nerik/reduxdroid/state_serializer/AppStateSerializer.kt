package com.github.s0nerik.reduxdroid.state_serializer

import android.content.Context
import com.github.s0nerik.reduxdroid.core.StateStore
import com.github.s0nerik.reduxdroid.core.state.AppState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.CBOR
import kotlinx.serialization.context.MutableSerialContextImpl
import kotlinx.serialization.context.SerialContext
import kotlin.reflect.KClass

interface AppStateSerializer {
    fun save()
    fun restore()
}

internal class AppStateSerializerImpl(
        private val store: StateStore,
        private val ctx: Context,
        private val serializers: Map<KClass<Any>, KSerializer<Any>>
) : AppStateSerializer {
    private val serialContext: SerialContext
    private val cbor: CBOR

    init {
        serialContext = MutableSerialContextImpl().apply {
            serializers.forEach {
                registerSerializer(it.key, it.value)
            }
        }

        cbor = CBOR(context = serialContext)
    }

    override fun save() {
        ctx.openFileOutput(APP_STATE_FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(cbor.dump(store.state))
            it.flush()
        }
    }

    override fun restore() {
        try {
            ctx.openFileInput(APP_STATE_FILE_NAME).use {
                val state = cbor.load<AppState>(it.readBytes())
                store.state = state
            }
            ctx.deleteFile(APP_STATE_FILE_NAME)
        } catch (ignore: Throwable) {}
    }

    companion object {
        private val APP_STATE_FILE_NAME = "_last_app_state"
    }
}