package com.github.s0nerik.reduxdroid.navigation

import androidx.annotation.IdRes

sealed class Nav {
    data class Forward(@IdRes val to: Int) : Nav()
    data class Back(@IdRes val to: Int? = null, val inclusive: Boolean = false) : Nav()
}

data class DidNavigate(
        @IdRes val graph: Int,
        @IdRes val from: Int,
        @IdRes val to: Int,
        private val graphStr: String = graph.toString(),
        private val fromStr: String = from.toString(),
        private val toStr: String = to.toString()
) {
    override fun toString() = "DidNavigate(graph: ${graphStr}, from: ${fromStr}, to: ${toStr})"
}