package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.actionConverter
import com.github.s0nerik.reduxdroid.testing.TestMiddleware
import com.github.s0nerik.reduxdroid.testing.testModules
import com.github.s0nerik.reduxdroid.util.ReduxConfig
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class ActionConverterMiddlewareTest : KoinTest {
    object Action1
    object Action2
    object Action3
    object Action4
    object Action5

    sealed class A {
        object A1 : A()
        object A2 : A()
    }

    private val dispatcher: ActionDispatcher by inject()
    private val testMiddleware: TestMiddleware by inject()

    @Before
    fun setup() {
        startKoin {
            modules(
                    Module().module,
                    *testModules().toTypedArray()
            )
        }
    }

    @After
    fun teardown() {
        stopKoin()
        ReduxConfig.clear()
    }

    @Test
    fun `actionConverter doesn't handle inheritance`() {
        loadKoinModules(
                module {
                    actionConverter<A> { _, _, dispatch -> dispatch(Action1) }
                    actionConverter<A.A1> { _, _, dispatch -> dispatch(Action2) }
                }
        )

        dispatcher.dispatch(A.A1)

        Assert.assertArrayEquals(arrayOf(A.A1, Action2), testMiddleware.actions.toTypedArray())
    }

    @Test
    fun `test simple case`() {
        loadKoinModules(
                module {
                    actionConverter<Action1> { _, _, dispatch -> dispatch(Action2) }
                }
        )

        dispatcher.dispatch(Action1)

        Assert.assertEquals(2, testMiddleware.actions.size)
    }

    @Test
    fun `multiple actionConverters don't produce original action multiple times`() {
        loadKoinModules(
                module {
                    actionConverter<Action1> { _, _, dispatch -> dispatch(Action2) }
                    actionConverter<Action1> { _, _, dispatch -> dispatch(Action3) }
                    actionConverter<Action1> { _, _, dispatch -> dispatch(Action4) }
                }
        )

        dispatcher.dispatch(Action1)

        Assert.assertArrayEquals(arrayOf(Action1, Action2, Action3, Action4), testMiddleware.actions.toTypedArray())
    }

    @Test
    fun `actionConverters can return actions that itself will be converted to another actions`() {
        loadKoinModules(
                module {
                    actionConverter<Action1> { _, _, dispatch -> dispatch(Action2) }
                    actionConverter<Action2> { _, _, dispatch -> dispatch(Action3) }
                    actionConverter<Action3> { _, _, dispatch -> dispatch(Action4) }
                }
        )

        dispatcher.dispatch(Action1)

        Assert.assertArrayEquals(arrayOf(Action1, Action2, Action3, Action4), testMiddleware.actions.toTypedArray())
    }

    @Test
    fun `actionConverter dispatches converted actions in registration order`() {
        loadKoinModules(
                module {
                    actionConverter<Action1> { _, _, dispatch -> dispatch(Action2) }
                    actionConverter<Action2> { _, _, dispatch -> dispatch(Action3) }
                    actionConverter<Action3> { _, _, dispatch -> dispatch(Action4) }
                    actionConverter<Action1> { _, _, dispatch -> dispatch(Action2) }
                }
        )

        dispatcher.dispatch(Action1)

        Assert.assertArrayEquals(arrayOf(Action1, Action2, Action3, Action4, Action2, Action3, Action4), testMiddleware.actions.toTypedArray())
    }

    @Test
    fun `actionConverter can dispatch multiple actions, each actionConverter maintains dispatch orders`() {
        loadKoinModules(
                module {
                    actionConverter<Action1> { _, _, dispatch ->
                        dispatch(Action2)
                        dispatch(Action3)
                    }
                    actionConverter<Action2> { _, _, dispatch ->
                        dispatch(Action4)
                        dispatch(Action5)
                    }
                }
        )

        dispatcher.dispatch(Action1)

        Assert.assertArrayEquals(arrayOf(Action1, Action2, Action4, Action5, Action3), testMiddleware.actions.toTypedArray())
    }
}