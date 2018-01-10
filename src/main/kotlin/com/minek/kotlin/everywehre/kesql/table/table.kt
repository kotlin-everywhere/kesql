package com.minek.kotlin.everywehre.kesql.table

import com.minek.kotlin.everywehre.kesql.Database
import com.minek.kotlin.everywehre.kesql.orm.Model
import com.minek.kotlin.everywehre.kesql.sql.Condition
import com.minek.kotlin.everywehre.kesql.sql.EqualCondition
import com.minek.kotlin.everywehre.kesql.sql.SelectIdentifier
import com.minek.kotlin.everywehre.kesql.sql.quote
import com.minek.kotlin.everywehre.kesql.types.ColumnType
import com.minek.kotlin.everywehre.kesql.variableName
import java.util.concurrent.atomic.AtomicInteger
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface AbstractTableMetadata {
    val name: String
    val quoteName: String
        get() = quote(name)
    val sql: String
        get() = quoteName

    val columns: List<Column<*>>

    val primaryKeys: List<Column<*>>

    var database: Database

    fun initialize(database: Database)

    fun filterColumns(instance: Any): List<Column<*>> {
        return instance.javaClass.methods
                .filter {
                    it.typeParameters.isEmpty() &&
                            it.name.matches(getterRegex) &&
                            Column::class.java.isAssignableFrom(it.returnType)
                }
                .map { it ->
                    val column = it.invoke(instance) as Column<*>
                    if (column.name == "") {
                        column.name = variableName(it.name.substring("get".length))
                    }
                    column to column.orderOfAssign
                }
                .sortedBy { it.second }
                .map { it.first }
    }

    companion object {
        private val getterRegex = Regex("^get.+$")
    }
}

class TableMetadata(val table: Table) : AbstractTableMetadata {
    override val name: String = variableName(table.javaClass.simpleName)

    override val columns: List<Column<*>> by lazy {
        val columns = filterColumns(table)
        columns.forEach { it.table = table }
        columns
    }

    override val primaryKeys: List<Column<*>> by lazy {
        columns.filter { it.primaryKey }
    }


    override var database by Delegates.notNull<Database>()

    private var initialized = false

    override fun initialize(database: Database) {
        if (initialized) {
            return
        }
        initialized = true
        columns
        this.database = database
    }
}

interface AbstractTable {
    val meta: AbstractTableMetadata
}

abstract class Table : AbstractTable {
    override val meta = TableMetadata(this)
}

class Column<T : Any>(val type: ColumnType<T>, name: String = "", val primaryKey: Boolean = false) : SelectIdentifier<T>, ReadWriteProperty<Model, T> {
    override val sql: String
        get() = table.meta.sql + "." + quoteName

    val quoteName: String
        get() = quote(name)

    var name: String = name
        internal set

    var table: AbstractTable by Delegates.notNull()

    val orderOfAssign: Int

    init {
        orderOfAssign = sequenceForOrder.incrementAndGet()
    }

    infix fun eq(t: T): Condition {
        return EqualCondition(this, type.getIdentifier(t))
    }

    companion object {
        val sequenceForOrder = AtomicInteger()
    }

    override fun getValue(thisRef: Model, property: KProperty<*>): T {
        return thisRef.metadata.get(this)
    }

    override fun setValue(thisRef: Model, property: KProperty<*>, value: T) {
        thisRef.metadata.set(this, value)
    }
}