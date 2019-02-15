package com.github.s0nerik.reduxdroid.core

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.inject

class ActionDispatcherTest : KoinTest {
    private val actionDispatcher: ActionDispatcher by inject()

    private val modules
        get() = listOf(
                Module().module
        )

    lateinit var koinApp: KoinApplication

    @Before
    fun before() {
        koinApp = startKoin { modules(modules) }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `check DI hierarchy`() {
        koinApp.checkModules()
    }

    @Test
    fun `ActionDispatcher resolves successfully`() {
        actionDispatcher.dispatch(Unit)
    }
}