package com.github.s0nerik.reduxdroid.navigation

import androidx.annotation.IdRes

sealed class Nav {
    data class Forward @PublishedApi internal constructor(@IdRes val to: Int) : Nav()
    data class Back @PublishedApi internal constructor(@IdRes val to: Int? = null, val inclusive: Boolean = false) : Nav()
}

sealed class DidNavigate {
    data class Forward internal constructor(
            @IdRes val from: Int,
            @IdRes val to: Int
    ) : DidNavigate()

    data class Back internal constructor(
            @IdRes val from: Int,
            @IdRes val to: Int
    ) : DidNavigate()
}