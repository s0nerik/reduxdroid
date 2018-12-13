package com.github.s0nerik.reduxdroid.navigation.di

import androidx.annotation.IdRes
import com.github.s0nerik.reduxdroid.core.di.actionConverter
import com.github.s0nerik.reduxdroid.navigation.DidNavigate
import org.koin.dsl.context.ModuleDefinition

//region Any direction
fun ModuleDefinition.convertNav(@IdRes to: Int, @IdRes vararg from: Int, actionProvider: () -> Any) {
        convertNavForward(to, *from, actionProvider = actionProvider)
        convertNavBack(to, *from, actionProvider = actionProvider)
}

fun ModuleDefinition.convertNavFrom(@IdRes vararg id: Int, actionProvider: () -> Any) {
        convertNavForwardFrom(*id, actionProvider = actionProvider)
        convertNavBackFrom(*id, actionProvider = actionProvider)
}

fun ModuleDefinition.convertNavTo(@IdRes id: Int, actionProvider: () -> Any) {
        convertNavForwardTo(id, actionProvider = actionProvider)
        convertNavBackTo(id, actionProvider = actionProvider)
}
//endregion

//region Forward
fun ModuleDefinition.convertNavForward(@IdRes to: Int, @IdRes vararg from: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate.Forward>(
                filter = { it.to == to && it.from in from },
                converter = { actionProvider() }
        )

fun ModuleDefinition.convertNavForwardFrom(@IdRes vararg id: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate.Forward>(
                filter = { it.from in id },
                converter = { actionProvider() }
        )

fun ModuleDefinition.convertNavForwardTo(@IdRes id: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate.Forward>(
                filter = { it.to == id },
                converter = { actionProvider() }
        )
//endregion

//region Back
fun ModuleDefinition.convertNavBack(@IdRes to: Int, @IdRes vararg from: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate.Back>(
                filter = { it.to == to && it.from in from },
                converter = { actionProvider() }
        )

fun ModuleDefinition.convertNavBackFrom(@IdRes vararg id: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate.Back>(
                filter = { it.from in id },
                converter = { actionProvider() }
        )

fun ModuleDefinition.convertNavBackTo(@IdRes id: Int, actionProvider: () -> Any) =
        actionConverter<DidNavigate.Back>(
                filter = { it.to == id },
                converter = { actionProvider() }
        )
//endregion