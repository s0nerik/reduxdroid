package com.github.s0nerik.reduxdroid.navigation.di

import androidx.annotation.IdRes
import com.github.s0nerik.reduxdroid.core.di.actionConverter
import com.github.s0nerik.reduxdroid.navigation.DidNavigate
import org.koin.dsl.context.ModuleDefinition

fun ModuleDefinition.convertNavFrom(@IdRes id: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate>(
                filter = { it.to == id },
                converter = { actionProvider() }
        )

fun ModuleDefinition.convertNavTo(@IdRes id: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate>(
                filter = { it.to == id },
                converter = { actionProvider() }
        )

fun ModuleDefinition.convertNavForwardFrom(@IdRes id: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate.Forward>(
                filter = { it.from == id },
                converter = { actionProvider() }
        )

fun ModuleDefinition.convertNavForwardTo(@IdRes id: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate.Forward>(
                filter = { it.to == id },
                converter = { actionProvider() }
        )

fun ModuleDefinition.convertNavBackFrom(@IdRes id: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate.Back>(
                filter = { it.from == id },
                converter = { actionProvider() }
        )

fun ModuleDefinition.convertNavBackTo(@IdRes id: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate.Back>(
                filter = { it.to == id },
                converter = { actionProvider() }
        )