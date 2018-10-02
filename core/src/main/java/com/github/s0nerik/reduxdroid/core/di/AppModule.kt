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
        private val definition: ModuleDefinition.() -> Unit
) : ContentProvider() {
    companion object {
        private val modules = mutableListOf<Module>()

        val registeredModules: List<Module>
            get() = modules
    }

    override fun onCreate(): Boolean {
        val module = module(Path.ROOT, false, false, definition)
        modules += module
        return true
    }

    override fun query(uri: Uri, strings: Array<String>?, s: String?, strings1: Array<String>?, s1: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? = null
    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int= 0
    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int = 0
}