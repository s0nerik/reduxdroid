package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.combinedReducer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.checkModules

class ActionDispatcherTest : KoinTest {
    private val actionDispatcher: ActionDispatcher by inject()

    private val modules
        get() = listOf(
                Module().module,
                module {
                    combinedReducer()
                }
        )

    @Before
    fun before() {
        startKoin(modules)
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `check DI hierarchy`() {
        checkModules(modules)
    }

    @Test
    fun `ActionDispatcher resolves successfully`() {
        actionDispatcher.dispatch(Unit)
    }
}