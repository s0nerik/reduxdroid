package com.github.s0nerik.reduxdroid.permissions

import com.github.s0nerik.reduxdroid.core.di.actionConverter
import org.koin.dsl.context.ModuleDefinition

inline fun <reified RequestAction : Any> ModuleDefinition.requestPermissions(
        requestCode: Int,
        permissions: List<String>,
        crossinline grantedConverter: (RequestPermissionsResult) -> Any,
        noinline deniedConverter: ((RequestPermissionsResult) -> Any)? = null
) {
    actionConverter<RequestAction> {
        RequestPermissions(permissions, requestCode)
    }

    actionConverter<RequestPermissionsResult>(
            filter = { it.requestCode == requestCode && it.allGranted },
            converter = grantedConverter
    )

    if (deniedConverter != null) {
        actionConverter<RequestPermissionsResult>(
                filter = { it.requestCode == requestCode && !it.allGranted },
                converter = deniedConverter
        )
    }
}