package com.github.s0nerik.reduxdroid.navigation.di

import androidx.annotation.IdRes
import com.github.s0nerik.reduxdroid.core.di.ActionConverter
import com.github.s0nerik.reduxdroid.core.di.actionConverter
import com.github.s0nerik.reduxdroid.navigation.Nav
import org.koin.dsl.context.ModuleDefinition

typealias NavBinder<A> = (A) -> Int

@PublishedApi
internal inline fun <reified A> ModuleDefinition._bindNavForward(crossinline binder: NavBinder<A>) {
    val converter: ActionConverter<A, Nav.Forward> = { Nav.Forward(binder(it)) }
    actionConverter(converter)
}

@PublishedApi
internal inline fun <reified A> ModuleDefinition._bindNavBack(inclusive: Boolean, noinline binder: NavBinder<A>?) {
    val converter: ActionConverter<A, Nav.Back> = { Nav.Back(binder?.invoke(it), inclusive = inclusive) }
    actionConverter(converter)
}

inline fun <reified A> ModuleDefinition.navForward(
        @IdRes navId: Int
) = _bindNavForward<A> { navId }

inline fun <reified A> ModuleDefinition.navForward(
        crossinline binder: NavBinder<A>
) = _bindNavForward(binder)

inline fun <reified A> ModuleDefinition.navBack(
        inclusive: Boolean = false,
        @IdRes navId: Int
) = _bindNavBack<A>(inclusive) { navId }

inline fun <reified A> ModuleDefinition.navBack(
        inclusive: Boolean = false,
        noinline binder: NavBinder<A>
) = _bindNavBack(inclusive, binder)

inline fun <reified A> ModuleDefinition.navBack(
        inclusive: Boolean = false
) = _bindNavBack<A>(inclusive, null)