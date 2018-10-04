package com.github.s0nerik.reduxdroid.navigation.middleware

import android.content.Context
import android.util.Log
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
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
) : NavigationMiddleware(), androidx.navigation.Navigator.OnNavigatorNavigatedListener, KoinComponent {
    private val dispatcher: ActionDispatcher by inject()

    private val _navControllers = mutableListOf<WeakReference<NavController>>()

    private var lastDestId: Int = 0
    private var currDestId: Int = 0

    override fun attachNavController(navController: NavController) {
        _navControllers += WeakReference(navController)

        val navigator = navController.navigatorProvider.getNavigator(FragmentNavigator::class.java)
        navigator.removeOnNavigatorNavigatedListener(this)
        navigator.addOnNavigatorNavigatedListener(this)
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
                error("Can't navigate back from ${idName(currDestId)} to ${idName(action.to)}")
            } else {
                error("Can't navigate back from ${idName(currDestId)}")
            }
        }
    }

    private fun idName(@IdRes resId: Int): String {
        val fullName = try { ctx.resources.getResourceName(resId) } catch (t: Throwable) { null }
        return (fullName ?: "NONE").substringAfterLast(":")
    }

    override fun onNavigatorNavigated(navigator: androidx.navigation.Navigator<*>, destId: Int, backStackEffect: Int) {
        lastDestId = currDestId
        currDestId = destId

        val effect = when (backStackEffect) {
            androidx.navigation.Navigator.BACK_STACK_DESTINATION_ADDED -> "ADDED"
            androidx.navigation.Navigator.BACK_STACK_DESTINATION_POPPED -> "POP"
            androidx.navigation.Navigator.BACK_STACK_UNCHANGED -> "UNCHANGED"
            else -> "UNKNOWN"
        }

        val lastDestStr = idName(lastDestId)
        val destStr = idName(destId)

        Log.d("NavigationMiddleware", "onNavigatorNavigated: $destStr, backStackEffect: $effect")

        if (effect == "POP") {
            Log.d("NavigationMiddleware", "DidNavigate.Back(from: $lastDestStr, to: $destStr)")
            dispatcher.dispatch(DidNavigate.Back(lastDestId, destId))
        } else if (effect == "ADDED") {
            Log.d("NavigationMiddleware", "DidNavigate.Forward(from: $lastDestStr, to: $destStr)")
            dispatcher.dispatch(DidNavigate.Forward(lastDestId, destId))
        }
    }
}