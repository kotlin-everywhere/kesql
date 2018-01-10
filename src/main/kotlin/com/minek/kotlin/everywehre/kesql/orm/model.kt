package com.minek.kotlin.everywehre.kesql.orm

import com.minek.kotlin.everywehre.kesql.Database
import com.minek.kotlin.everywehre.kesql.sql.Condition
import com.minek.kotlin.everywehre.kesql.sql.EqualCondition
import com.minek.kotlin.everywehre.kesql.sql.SelectIdentifier
import com.minek.kotlin.everywehre.kesql.sql.and_
import com.minek.kotlin.everywehre.kesql.table.AbstractTable
import com.minek.kotlin.everywehre.kesql.table.AbstractTableMetadata
import com.minek.kotlin.everywehre.kesql.table.Column
import com.minek.kotlin.everywehre.kesql.variableName
import kotlin.properties.Delegates


class ModelTableMetadata<T>(val table: ModelTable<T>, val modelClass: Class<T>) : AbstractTableMetadata {
    override val name by lazy {
        variableName(table.javaClass.name.split('.').last().replace(Regex("[$].+$"), ""))
    }

    override val columns: List<Column<*>> by lazy {
        val columns = filterColumns(table)
        columns.forEach { it.table = table }
        columns
    }

    override val primaryKeys: List<Column<*>> by lazy {
        columns.filter { it.primaryKey }
    }

    private var initialized = false

    override fun initialize(database: Database) {
        if (initialized) {
            return
        }
        name
        columns
        assert(primaryKeys.size != 0)
        this.database = database

        initialized = true
    }

    fun primaryKeyCondition(model: Model): Condition {
        return and_(
                primaryKeys.map {
                    EqualCondition(it, it.type.getIdentifier(model.metadata.property[it] as Any))
                }
        )
    }

    override var database: Database by Delegates.notNull()
}

abstract class ModelTable<T> : AbstractTable, SelectIdentifier<T> {
    override val meta = ModelTableMetadata(this, this.javaClass.enclosingClass as Class<T>)
    override val sql: String = ""
}


class ModelMetadata(val model: Model) {
    val property = hashMapOf<Column<*>, Any>()
    var isSynced = false
    val dirtyColumns = hashSetOf<Column<*>>()

    fun <T : Any> get(column: Column<T>): T {
        return property[column] as T
    }

    fun set(column: Column<*>, value: Any, dirty: Boolean = true) {
        property[column] = value
        if (dirty) {
            dirtyColumns.add(column)
        }
    }

    fun set(map: Map<Column<*>, Any>, dirty: Boolean = true) {
        map.forEach { set(it.key, it.value, dirty) }
    }

    fun clearDirty() {
        dirtyColumns.removeAll(dirtyColumns.toList())
    }
}


abstract class Model {
    val metadata: ModelMetadata = ModelMetadata(this)
}
