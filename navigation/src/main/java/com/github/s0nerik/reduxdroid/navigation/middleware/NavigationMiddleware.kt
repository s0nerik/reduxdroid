package com.github.s0nerik.reduxdroid.navigation.middleware

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.middleware.TypedMiddleware
import com.github.s0nerik.reduxdroid.navigation.DidNavigate
import com.github.s0nerik.reduxdroid.navigation.Nav
import kotlinx.coroutines.*
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.channels.Channel
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

abstract class NavigationMiddleware : TypedMiddleware<Nav>(Nav::class) {
    abstract fun attachNavController(navController: NavController)
    abstract fun detachNavController(navController: NavController)

    abstract fun attachActivity(activity: Activity)

    abstract var debug: Boolean
}

internal class NavigationMiddlewareImpl(
        private val ctx: Context
) : NavigationMiddleware(), NavController.OnDestinationChangedListener, KoinComponent, LifecycleObserver, CoroutineScope {
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override var debug: Boolean = false

    private val dispatcher: ActionDispatcher by inject()

    private val _navControllers = mutableListOf<WeakReference<NavController>>()

    private var activity: WeakReference<Activity> = WeakReference<Activity>(null)

    // Holds current graph destination ids in form of [graph id -> destination id]
    private var destinationsMap = mapOf<Int, Int>()

    private val destinationsMapReadable: Map<String, String>
        get() = destinationsMap.mapKeys { idName(it.key) }.mapValues { idName(it.value) }

    private val navActionsChannel = Channel<Nav>(Channel.UNLIMITED)

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        launch {
            awaitFrame()
            for (action in navActionsChannel) {
                tryNavigate(action)
                delay(NAVIGATION_DEBOUNCE_TIME_MS)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        job.cancelChildren()
    }

    override fun attachActivity(activity: Activity) {
        require(activity is LifecycleOwner) {
            "Only activities implementing `LifecycleOwner` interface are supported"
        }

        val oldActivity = this.activity.get()
        if (oldActivity != activity) {
            (oldActivity as? LifecycleOwner)?.lifecycle?.removeObserver(this)
        }

        activity.lifecycle.addObserver(this)
        this.activity = WeakReference(activity)
    }

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
        navActionsChannel.offer(action)
        return next(action)
    }

    private fun tryNavigate(action: Nav) {
        _navControllers.reversed().mapNotNull { it.get() }.forEach { navCtrl ->
            try {
                when (action) {
                    is Nav.Forward -> {
                        navCtrl.navigate(action.to, action.args, action.navOptions, action.navigatorExtras)
                        return
                    }
                    is Nav.Back -> {
                        val popped = action.to?.let { navCtrl.popBackStack(it, action.inclusive) } ?: navCtrl.popBackStack()
                        if (popped) {
                            return
                        }
                    }
                }
            } catch (ignore: Throwable) {}
        }

        when (action) {
            is Nav.Forward -> error("Can't navigate to ${idName(action.to)}. Current navigation state: ${destinationsMapReadable}")
            is Nav.Back -> activity.get()?.finish()
        }
    }

    private fun idName(@IdRes resId: Int): String {
        val fullName = try { "@${ctx.resources.getResourceName(resId).substringAfterLast(":")}" } catch (t: Throwable) { null }
        return (fullName ?: "NONE")
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        // val className = (destination as? FragmentNavigator.Destination)?.className

        destination.parent?.let { graph ->
            val newDestMap = destinationsMap + (graph.id to destination.id)
            if (newDestMap[graph.id] != destinationsMap[graph.id]) {
                val fromId = destinationsMap[graph.id] ?: 0
                val toId = newDestMap[graph.id] ?: 0

                dispatcher.dispatch(DidNavigate(graph.id, fromId, toId, idName(graph.id), idName(fromId), idName(toId)))
                destinationsMap = newDestMap
            }
        }

        if (debug) {
            Log.d("NavigationMiddleware", "onDestinationChanged, destinations: ${destinationsMapReadable}")
        }
    }

    companion object {
        /**
         * Makes navigation a bit more predictable in cases when two or more actions are dispatched in a quick succession.
         */
        private const val NAVIGATION_DEBOUNCE_TIME_MS = 100L
    }
}