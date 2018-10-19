package com.github.s0nerik.reduxdroid.core

import com.github.s0nerik.reduxdroid.core.di.actionConverter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.KoinContext
import org.koin.dsl.context.ModuleDefinition
import org.koin.standalone.StandAloneContext.koinContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest

class ActionConverterTest : KoinTest {
    object Action1
    object Action2
    object Action3

    @Before
    fun setup() {
        startKoin(listOf())
    }

    @After
    fun teardown() {
        stopKoin()
    }

    private fun testModule(func: ModuleDefinition.() -> Unit) {
        val moduleDefinition = ModuleDefinition(koinContext = koinContext as KoinContext)
        with(moduleDefinition, func)
    }

    @Test(expected = IllegalStateException::class)
    fun `only one ActionConverter(dropOriginalAction = true) can be registered`() {
        testModule {
            actionConverter<Action1>(dropOriginalAction = true) { Action2 }
            actionConverter<Action1>(dropOriginalAction = true) { Action3 }
        }
    }

    @Test
    fun `multiple ActionConverter(dropOriginalAction = false) can be registered`() {
        testModule {
            actionConverter<Action1>(dropOriginalAction = false) { Action2 }
            actionConverter<Action1>(dropOriginalAction = false) { Action3 }
        }
    }

    @Test
    fun `multiple ActionConverter(dropOriginalAction = false) can be registered with a single ActionConverter(dropOriginalAction = true)`() {
        testModule {
            actionConverter<Action1>(dropOriginalAction = true) { Action2 }
            actionConverter<Action1>(dropOriginalAction = false) { Action3 }
            actionConverter<Action1>(dropOriginalAction = false) { Action1 }
        }
    }
}