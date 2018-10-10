package com.github.s0nerik.reduxdroid.navigation.di

import androidx.annotation.IdRes
import com.github.s0nerik.reduxdroid.core.di.ActionConverter
import com.github.s0nerik.reduxdroid.core.di.actionConverter
import com.github.s0nerik.reduxdroid.navigation.Nav
import org.koin.dsl.context.ModuleDefinition

typealias NavBinder<A> = (A) -> Int

@PublishedApi
internal inline fun <reified A> ModuleDefinition._bindNavForward(dropOriginalAction: Boolean, crossinline binder: NavBinder<A>) {
    val converter: ActionConverter<A, Nav.Forward> = { Nav.Forward(binder(it)) }
    actionConverter(dropOriginalAction, converter)
}

@PublishedApi
internal inline fun <reified A> ModuleDefinition._bindNavBack(dropOriginalAction: Boolean, inclusive: Boolean, noinline binder: NavBinder<A>?) {
    val converter: ActionConverter<A, Nav.Back> = { Nav.Back(binder?.invoke(it), inclusive = inclusive) }
    actionConverter(dropOriginalAction, converter)
}

inline fun <reified A> ModuleDefinition.navForward(
        @IdRes navId: Int,
        dropOriginalAction: Boolean = true
) = _bindNavForward<A>(dropOriginalAction) { navId }

inline fun <reified A> ModuleDefinition.navForward(
        dropOriginalAction: Boolean = true,
        crossinline binder: NavBinder<A>
) = _bindNavForward(dropOriginalAction, binder)

inline fun <reified A> ModuleDefinition.navBack(
        @IdRes navId: Int,
        inclusive: Boolean = false,
        dropOriginalAction: Boolean = true
) = _bindNavBack<A>(dropOriginalAction, inclusive) { navId }

inline fun <reified A> ModuleDefinition.navBack(
        inclusive: Boolean = false,
        dropOriginalAction: Boolean = true,
        noinline binder: NavBinder<A>
) = _bindNavBack(dropOriginalAction, inclusive, binder)

inline fun <reified A> ModuleDefinition.navBack(
        inclusive: Boolean = false,
        dropOriginalAction: Boolean = true
) = _bindNavBack<A>(dropOriginalAction, inclusive, null)