package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.actionConverter
import com.github.s0nerik.reduxdroid.core.di.middlewares
import com.github.s0nerik.reduxdroid.core.middleware.middleware
import com.github.s0nerik.reduxdroid.core.state.AppState
import me.tatarka.redux.middleware.TestMiddleware
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest

class ActionConverterMiddlewareTest : KoinTest {
    object Action1
    object Action2
    object Action3
    object Action4

    private val store: StateStore by inject()
    private val dispatcher: ActionDispatcher by inject()

    private fun startTestKoin(testModuleDefinition: ModuleDefinition.() -> Unit): TestMiddleware<AppState, Any, Any> {
        lateinit var testMiddleware: TestMiddleware<AppState, Any, Any>

        startKoin(listOf(
                Module().module,
                module(definition = testModuleDefinition),
                module {
                    testMiddleware = TestMiddleware<AppState, Any, Any>(store)
                    middlewares {
                        listOf(
                                middleware<Any, Any> { next, action -> testMiddleware.dispatch(next, action) }
                        )
                    }
                }
        ))

        return testMiddleware
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `test simple case`() {
        val testMiddleware = startTestKoin {
            actionConverter<Action1>(dropOriginalAction = false) { Action2 }
        }

        dispatcher.dispatch(Action1)

        Assert.assertEquals(2, testMiddleware.actions().size)
    }

    @Test
    fun `test simple case with dropOriginalAction = true`() {
        val testMiddleware = startTestKoin {
            actionConverter<Action1>(dropOriginalAction = true) { Action2 }
        }

        dispatcher.dispatch(Action1)

        Assert.assertEquals(1, testMiddleware.actions().size)
    }

    @Test
    fun `if any ActionConverter for action T has dropOriginalAction = true, action T will be dismissed (1)`() {
        val testMiddleware = startTestKoin {
            actionConverter<Action1>(dropOriginalAction = true) { Action2 }
            actionConverter<Action1>(dropOriginalAction = true) { Action3 }
        }

        dispatcher.dispatch(Action1)

        Assert.assertArrayEquals(arrayOf(Action2, Action3), testMiddleware.actions().toTypedArray())
    }

    @Test
    fun `if any ActionConverter for action T has dropOriginalAction = true, action T will be dismissed (2)`() {
        val testMiddleware = startTestKoin {
            actionConverter<Action1>(dropOriginalAction = true) { Action2 }
            actionConverter<Action1>(dropOriginalAction = true) { Action3 }
            actionConverter<Action1>(dropOriginalAction = false) { Action4 }
        }

        dispatcher.dispatch(Action1)

        Assert.assertArrayEquals(arrayOf(Action2, Action3, Action4), testMiddleware.actions().toTypedArray())
    }

    @Test
    fun `multiple actionConverter(dropsOriginalAction = false) don't produce original action multiple times`() {
        val testMiddleware = startTestKoin {
            actionConverter<Action1>(dropOriginalAction = false) { Action2 }
            actionConverter<Action1>(dropOriginalAction = false) { Action3 }
            actionConverter<Action1>(dropOriginalAction = false) { Action4 }
        }

        dispatcher.dispatch(Action1)

        Assert.assertArrayEquals(arrayOf(Action1, Action2, Action3, Action4), testMiddleware.actions().toTypedArray())
    }
}