package com.github.s0nerik.reduxdroid.testing

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

// https://discuss.kotlinlang.org/t/unit-testing-coroutines/6453/6
@InternalCoroutinesApi
private class TestDirectContext : CoroutineDispatcher(), Delay {
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        continuation.resume(Unit)
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        block.run()
    }
}

@UseExperimental(InternalCoroutinesApi::class)
fun runBlockingTest(block: suspend CoroutineScope.() -> Any) = runBlocking(TestDirectContext()) {
    block()
    Unit
}