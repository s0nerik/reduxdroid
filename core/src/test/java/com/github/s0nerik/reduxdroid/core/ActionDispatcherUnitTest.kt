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

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ActionDispatcherUnitTest : KoinTest {
    private val actionDispatcher: ActionDispatcher by inject()
    private val nonConvertedActionDispatcher: ActionDispatcherImpl by inject(NON_CONVERTED_DISPATCHER)

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

    @Test
    fun `non-converted ActionDispatcher resolves successfully`() {
        nonConvertedActionDispatcher.dispatch(Unit)
    }

    @Test
    fun `non-converted ActionDispatcher is not the same as normal ActionDispatcher`() {
        Assert.assertNotSame(actionDispatcher, nonConvertedActionDispatcher)
    }
}