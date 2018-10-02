package com.github.s0nerik.reduxdroid.core.util

import kotlin.coroutines.CoroutineContext

interface ContextHolder {
    val main: CoroutineContext
    val io: CoroutineContext
    val bg: CoroutineContext
}

internal data class MainContextHolder(
        override val main: CoroutineContext,
        override val io: CoroutineContext,
        override val bg: CoroutineContext
) : ContextHolder