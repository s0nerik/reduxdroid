package com.github.s0nerik.reduxdroid.navigation.di

import androidx.annotation.IdRes
import com.github.s0nerik.reduxdroid.core.di.actionConverter
import com.github.s0nerik.reduxdroid.navigation.DidNavigate
import org.koin.dsl.context.ModuleDefinition

fun ModuleDefinition.convertNav(@IdRes graph: Int, @IdRes from: Int, @IdRes to: Int, actionProvider: () -> Any) {
    actionConverter<DidNavigate>(filter = { it.graph == graph && it.from == from && it.to == to }) {
        actionProvider()
    }
}

fun ModuleDefinition.convertNavFrom(@IdRes graph: Int, @IdRes from: Int, actionProvider: () -> Any) {
    actionConverter<DidNavigate>(filter = { it.graph == graph && it.from == from }) {
        actionProvider()
    }
}

fun ModuleDefinition.convertNavTo(@IdRes graph: Int, @IdRes to: Int, actionProvider: () -> Any) {
    actionConverter<DidNavigate>(filter = { it.graph == graph && it.to == to }) {
        actionProvider()
    }
}