package com.github.s0nerik.reduxdroid.activity_result

import android.app.Activity
import android.content.Intent
import com.github.s0nerik.reduxdroid.core.di.actionConverter
import org.koin.dsl.context.ModuleDefinition

inline fun <reified RequestAction : Any> ModuleDefinition.startForResult(
        requestCode: Int,
        crossinline intentProvider: (RequestAction) -> Intent,
        crossinline okConverter: (ActivityResult) -> Any,
        noinline canceledConverter: ((ActivityResult) -> Any)? = null
) {
    actionConverter<RequestAction> {
        StartActivityForResult(intentProvider(it), requestCode)
    }

    actionConverter<ActivityResult>(
            filter = { it.requestCode == requestCode && it.resultCode == Activity.RESULT_OK },
            converter = { okConverter(it) }
    )

    if (canceledConverter != null) {
        actionConverter<ActivityResult>(
                filter = { it.requestCode == requestCode && it.resultCode == Activity.RESULT_CANCELED },
                converter = { canceledConverter(it) }
        )
    }
}