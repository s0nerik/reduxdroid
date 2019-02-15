package com.github.s0nerik.reduxdroid.testing

import com.github.s0nerik.reduxdroid.core.ActionDispatcher
import com.github.s0nerik.reduxdroid.core.di.AppModule
import com.github.s0nerik.reduxdroid.core.middleware.Middleware
import io.github.classgraph.ClassGraph
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Finds and instantiates all AppModules in project.
 *
 * @param whitelistPackages packages to search for AppModule descendants. Don't forget to put your
 * main package name here, otherwise the AppModules declared in it wouldn't be initialized.
 */
fun appModules(vararg whitelistPackages: String): List<Module> {
    val classGraph = ClassGraph().apply {
        enableAllInfo()
        whitelistPackages("com.github.s0nerik.reduxdroid")
        whitelistPackages(*whitelistPackages)
    }
    val scanResult = classGraph.scan()

    val appModules = scanResult.getSubclasses(AppModule::class.qualifiedName)

    val registeredModules = mutableListOf<Module>()

    appModules.forEach {
        val kClass = Class.forName(it.name).kotlin
        val instance = kClass.constructors.first().call()
        registeredModules += (instance as AppModule).module
    }

    return registeredModules
}

/**
 * Overrides [ActionDispatcher] definition with an instance configured for tests.
 *
 * @return a module that provides a [TestMiddleware] and overrides [ActionDispatcher] to use that [TestMiddleware].
 */
fun testModules(
        applyActionConverter: Boolean = true,
        applyAppMiddlewares: Boolean = true,
        extraMiddlewares: () -> List<Middleware<Any, Any>> = { emptyList() }
): List<Module> = listOf(
        module {
            single { TestMiddleware(get()) }
            single(override = true) {
                testActionDispatcher(
                        store = get(),
                        testMiddleware = get(),
                        withActionConverter = applyActionConverter,
                        applyAppMiddlewares = applyAppMiddlewares,
                        extraMiddlewares = extraMiddlewares()
                )
            }
        }
)