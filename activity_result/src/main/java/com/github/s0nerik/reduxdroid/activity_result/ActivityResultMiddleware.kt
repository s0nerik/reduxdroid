package com.github.s0nerik.reduxdroid.activity_result

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.middleware.TypedMiddleware
import com.github.s0nerik.reduxdroid.util.weak
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * This action is used to start activity for result.
 *
 * Intended use-cases:
 * - start another app's activity for result
 * - start 3rd party library activity for result
 *
 * Propagating result of anything else than mentioned above is highly discouraged. For those cases
 * it's highly advised to do it by changing [AppState] using specific action reducer.
 *
 * @see [ActivityResult]
 */
data class StartActivityForResult(
        val intent: Intent,
        val requestCode: Int
)

data class ActivityResult internal constructor(
        val requestCode: Int,
        val resultCode: Int,
        val data: Intent?
)

abstract class ActivityResultMiddleware : TypedMiddleware<StartActivityForResult>(StartActivityForResult::class) {
    abstract fun attach(activity: Activity)
    abstract fun notifyResult(requestCode: Int, resultCode: Int, data: Intent?)

    abstract var debugMode: Boolean
}

internal class ActivityResultMiddlewareImpl : ActivityResultMiddleware(), KoinComponent {
    private val dispatcher: ActionDispatcher by inject()

    private var currentActivity by weak<Activity>()

    override var debugMode: Boolean = false

    override fun attach(activity: Activity) {
        currentActivity = activity
    }

    override fun notifyResult(requestCode: Int, resultCode: Int, data: Intent?) {
        dispatcher.dispatch(ActivityResult(requestCode = requestCode, resultCode = resultCode, data = data))
    }

    override fun run(next: (Any) -> Any, action: StartActivityForResult): Any {
        currentActivity?.let { activity ->
            if (action.intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivityForResult(action.intent, action.requestCode)
            } else if (debugMode) {
                Log.e("ActivityResultMW", "There's no activity that can handle a given intent. Action: ${action}")
            }
        }
        return next(action)
    }
}