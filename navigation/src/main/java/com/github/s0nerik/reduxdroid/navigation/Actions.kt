package com.github.s0nerik.reduxdroid.navigation

import androidx.annotation.IdRes

sealed class Nav {
    /**
     * Dispatching this action will result in navigation to a specified destination.
     *
     * **CAUTION**: This action is intended to be instantiated inside an `actionConverter` definition.
     */
    data class Forward(@IdRes val to: Int) : Nav()

    /**
     * Dispatching this action will result in back navigation (as if the "Back" button was pressed).
     *
     * *CAUTION*: This action is intended to be instantiated inside an `actionConverter` definition.
     */
    data class Back(@IdRes val to: Int? = null, val inclusive: Boolean = false) : Nav()
}

data class DidNavigate internal constructor(
        @IdRes val graph: Int,
        @IdRes val from: Int,
        @IdRes val to: Int,
        private val graphStr: String = graph.toString(),
        private val fromStr: String = from.toString(),
        private val toStr: String = to.toString()
) {
    override fun toString() = "DidNavigate(graph: ${graphStr}, from: ${fromStr}, to: ${toStr})"
}