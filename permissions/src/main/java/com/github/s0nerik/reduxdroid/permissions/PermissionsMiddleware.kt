package com.github.s0nerik.reduxdroid.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.middleware.TypedMiddleware
import com.github.s0nerik.reduxdroid.util.weak
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

data class RequestPermissions(
        val permissions: List<String>,
        @IntRange(from = 0)
        val requestCode: Int
)

data class RequestPermissionsResult internal constructor(
        val requestCode: Int,
        /**
         * Request results in form of (Permission -> Is granted?) mapping
         */
        val results: Map<String, Boolean>
) {
    internal constructor(requestCode: Int, permissions: Iterable<String>, grantResults: Iterable<Int>) : this(
            requestCode = requestCode,
            results = permissions.zip(grantResults) { p, r ->
                Pair(p, r == PackageManager.PERMISSION_GRANTED)
            }.toMap()
    )

    val allGranted: Boolean
        get() = results.values.all { it }
}

abstract class PermissionsMiddleware : TypedMiddleware<RequestPermissions>(RequestPermissions::class) {
    abstract fun attach(activity: Activity)
    abstract fun notifyResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)

    abstract var debugMode: Boolean
}

internal class PermissionsMiddlewareImpl : PermissionsMiddleware(), KoinComponent {
    private val dispatcher: ActionDispatcher by inject()

    private var currentActivity by weak<Activity>()

    override var debugMode: Boolean = false

    override fun attach(activity: Activity) {
        currentActivity = activity
    }

    override fun notifyResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val resultAction = RequestPermissionsResult(requestCode, permissions.asIterable(), grantResults.asIterable())
        if (debugMode) {
            Log.d("PermissionsMiddleware", "Permission request results: ${resultAction.results}")
        }
        dispatcher.dispatch(resultAction)
    }

    override fun run(next: (Any) -> Any, action: RequestPermissions): Any {
        currentActivity?.let { activity ->
            var requestPermissionsNeeded = false
            action.permissions.forEach {
                if (ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionsNeeded = true
                }
            }
            if (requestPermissionsNeeded) {
                ActivityCompat.requestPermissions(activity, action.permissions.toTypedArray(), action.requestCode)
                if (debugMode) {
                    Log.d("PermissionsMiddleware", "Requesting permissions: ${action.permissions}")
                }
            } else {
                val results = mutableMapOf<String, Boolean>()
                action.permissions.forEach { results[it] = true }
                next(RequestPermissionsResult(action.requestCode, results))
                if (debugMode) {
                    Log.d("PermissionsMiddleware", "Requested permissions are already granted. Permissions: ${action.permissions}")
                }
            }
        }
        return next(action)
    }
}