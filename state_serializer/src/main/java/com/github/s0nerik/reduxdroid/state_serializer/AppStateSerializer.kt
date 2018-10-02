package com.github.s0nerik.reduxdroid.state_serializer

import android.content.Context
import com.github.s0nerik.reduxdroid.core.StateStore
import com.github.s0nerik.reduxdroid.core.state.AppState
import kotlinx.serialization.cbor.CBOR

interface AppStateSerializer {
    fun save()
    fun restore()
}

internal class AppStateSerializerImpl(
        private val store: StateStore,
        private val ctx: Context
) : AppStateSerializer {
    override fun save() {
        ctx.openFileOutput(APP_STATE_FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(CBOR.dump(store.state))
            it.flush()
        }
    }

    override fun restore() {
        try {
            ctx.openFileInput(APP_STATE_FILE_NAME).use {
                val state = CBOR.load<AppState>(it.readBytes())
                store.state = state
            }
            ctx.deleteFile(APP_STATE_FILE_NAME)
        } catch (ignore: Throwable) {}
    }

    companion object {
        private val APP_STATE_FILE_NAME = "_last_app_state"
    }
}