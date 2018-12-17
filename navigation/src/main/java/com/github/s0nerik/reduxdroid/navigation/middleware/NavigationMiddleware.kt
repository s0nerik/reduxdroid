package com.github.s0nerik.reduxdroid.navigation.middleware

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.middleware.TypedMiddleware
import com.github.s0nerik.reduxdroid.navigation.DidNavigate
import com.github.s0nerik.reduxdroid.navigation.Nav
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.lang.ref.WeakReference

abstract class NavigationMiddleware : TypedMiddleware<Nav>(Nav::class) {
    abstract fun attachNavController(navController: NavController)
    abstract fun detachNavController(navController: NavController)
}

internal class NavigationMiddlewareImpl(
        private val ctx: Context
) : NavigationMiddleware(), NavController.OnDestinationChangedListener, KoinComponent {
    private val dispatcher: ActionDispatcher by inject()

    private val _navControllers = mutableListOf<WeakReference<NavController>>()

    private var lastDestinationId: Int = 0

    override fun attachNavController(navController: NavController) {
        _navControllers += WeakReference(navController)

        navController.removeOnDestinationChangedListener(this)
        navController.addOnDestinationChangedListener(this)
    }

    override fun detachNavController(navController: NavController) {
        _navControllers.removeAll {
            val ref = it.get()
            ref == null || ref == navController
        }
    }

    override fun run(next: (Any) -> Any, action: Nav): Any {
        tryNavigate(action)
        return next(action)
    }

    private fun tryNavigate(action: Nav) {
        _navControllers.reversed().mapNotNull { it.get() }.forEach { navCtrl ->
            try {
                when (action) {
                    is Nav.Forward -> navCtrl.navigate(action.to)
                    is Nav.Back -> action.to?.let { navCtrl.popBackStack(it, action.inclusive) } ?: navCtrl.popBackStack()
                }
                return
            } catch (ignore: Throwable) {}
        }

        when (action) {
            is Nav.Forward -> error("Can't navigate to ${idName(action.to)}")
            is Nav.Back -> if (action.to != null) {
                error("Can't navigate back from ${idName(lastDestinationId)} to ${idName(action.to)}")
            } else {
                error("Can't navigate back from ${idName(lastDestinationId)}")
            }
        }
    }

    private fun idName(@IdRes resId: Int): String {
        val fullName = try { "@${ctx.resources.getResourceName(resId)}" } catch (t: Throwable) { null }
        return (fullName ?: "NONE").substringAfterLast(":")
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        val lastDestStr = idName(lastDestinationId)
        val destStr = idName(destination.id)

        Log.d("NavigationMiddleware", "DidNavigate(from: $lastDestStr, to: $destStr)")
        try {
            dispatcher.dispatch(DidNavigate(lastDestinationId, destination.id))
        } catch (t: Throwable) {
            Log.e("NavigationMiddleware", "Can't dispatch DidNavigate(from: $lastDestStr, to: $destStr). Cause: $t")
        }

        lastDestinationId = destination.id
    }
}