package com.github.s0nerik.reduxdroid.core.di

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import org.koin.core.module.Module
import org.koin.dsl.module

abstract class AppModule(
        definition: Module.() -> Unit,
        position: Position = Position.MIDDLE,
        createOnStart: Boolean = false,
        override: Boolean = false
) : ContentProvider() {
    companion object {
        private val moduleEntries = mutableListOf<ModuleEntry>()

        val registeredModules: List<Module>
            get() = moduleEntries.sortedBy { it.position }.map { it.module }
    }

    internal data class ModuleEntry(
            val module: Module,
            val position: Position
    )

    enum class Position {
        START, MIDDLE, END
    }

    internal var moduleEntry: ModuleEntry

    val module: Module
        get() = moduleEntry.module

    init {
        moduleEntry = ModuleEntry(
                position = position,
                module = module(createdAtStart = createOnStart, override = override, moduleDeclaration = definition)
        )
    }

    override fun onCreate(): Boolean {
        moduleEntries += moduleEntry
        return true
    }

    override fun query(uri: Uri, strings: Array<String>?, s: String?, strings1: Array<String>?, s1: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? = null
    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int= 0
    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int = 0
}