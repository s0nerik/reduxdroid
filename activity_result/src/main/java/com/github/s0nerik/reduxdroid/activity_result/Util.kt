package com.github.s0nerik.reduxdroid.activity_result

import android.app.Activity
import android.content.Intent
import com.github.s0nerik.reduxdroid.core.di.actionConverter
import org.koin.core.module.Module

inline fun <reified RequestAction : Any> Module.startForResult(
        requestCode: Int,
        crossinline intentProvider: (RequestAction) -> Intent,
        crossinline okConverter: (ActivityResult) -> Any,
        noinline canceledConverter: ((ActivityResult) -> Any)? = null
) {
    actionConverter<RequestAction> { action, _, dispatch ->
        dispatch(StartActivityForResult(intentProvider(action), requestCode))
    }

    actionConverter<ActivityResult> { action, _, dispatch ->
        if (action.requestCode == requestCode) {
            if (action.resultCode == Activity.RESULT_OK) {
                dispatch(okConverter(action))
            } else if (canceledConverter != null && action.resultCode == Activity.RESULT_CANCELED) {
                dispatch(canceledConverter(action))
            }
        }
    }
}