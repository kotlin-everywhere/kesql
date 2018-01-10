package com.minek.kotlin.everywehre.kesql

import com.minek.kotlin.everywehre.kesql.orm.ModelMetadata
import com.minek.kotlin.everywehre.kesql.orm.ModelTable
import com.minek.kotlin.everywehre.kesql.session.Session
import com.minek.kotlin.everywehre.kesql.session.ThreadSession
import com.minek.kotlin.everywehre.kesql.table.AbstractTable
import kotlin.properties.Delegates

open class Database {
    private var tables = listOf<AbstractTable>()
    val session: Session = ThreadSession(this)
    private var initialized = false
    var url: String by Delegates.notNull()

    fun addTables(tables: List<AbstractTable>) {
        this.tables = this.tables + tables
    }

    fun initialize(url: String) {
        if (initialized) {
            return
        }
        this.url = url
        tables.forEach { it.meta.initialize(this) }
        initialized = true
    }

    operator fun get(metadata: ModelMetadata): ModelTable<*> {
        return tables
                .filter { it is ModelTable<*> }
                .map { it as ModelTable<*> }
                .first { it.meta.modelClass == metadata.model.javaClass }
    }
}
