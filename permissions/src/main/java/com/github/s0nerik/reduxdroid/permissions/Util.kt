package com.github.s0nerik.reduxdroid.permissions

import com.github.s0nerik.reduxdroid.core.di.actionConverter
import org.koin.dsl.context.ModuleDefinition

inline fun <reified RequestAction : Any> ModuleDefinition.requestPermissions(
        requestCode: Int,
        permissions: List<String>,
        crossinline grantedConverter: (RequestPermissionsResult) -> Any,
        noinline deniedConverter: ((RequestPermissionsResult) -> Any)? = null
) {
    actionConverter<RequestAction> { _, _, dispatch ->
        dispatch(RequestPermissions(permissions, requestCode))
    }

    actionConverter<RequestPermissionsResult> { action, _, dispatch ->
        if (action.requestCode == requestCode) {
            if (action.allGranted) {
                dispatch(grantedConverter(action))
            } else if (deniedConverter != null) {
                dispatch(deniedConverter(action))
            }
        }
    }
}