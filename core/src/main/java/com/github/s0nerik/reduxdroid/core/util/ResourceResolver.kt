package com.github.s0nerik.reduxdroid.core.util

import android.app.Application
import androidx.annotation.StringRes

interface ResourceResolver {
    fun str(@StringRes id: Int): String
}

internal class ResourceResolverImpl(
        private val app: Application
) : ResourceResolver {
    override fun str(id: Int) = app.getString(id)
}