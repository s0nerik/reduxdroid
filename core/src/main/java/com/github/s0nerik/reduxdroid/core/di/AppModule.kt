package com.github.s0nerik.reduxdroid.core.di

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.dsl.path.Path

abstract class AppModule(
        definition: ModuleDefinition.() -> Unit,
        path: String = Path.ROOT,
        createOnStart: Boolean = false,
        override: Boolean = false
) : ContentProvider() {
    companion object {
        private val modules = mutableListOf<Module>()

        val registeredModules: List<Module>
            get() = modules
    }

    var module: Module
        get
        private set

    init {
        module = module(path = path, createOnStart = createOnStart, override = override, definition = definition)
    }

    override fun onCreate(): Boolean {
        modules += module
        return true
    }

    override fun query(uri: Uri, strings: Array<String>?, s: String?, strings1: Array<String>?, s1: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? = null
    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int= 0
    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int = 0
}