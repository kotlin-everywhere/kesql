package com.minek.kotlin.everywehre.kesql.sql

import com.minek.kotlin.everywehre.kesql.compact
import com.minek.kotlin.everywehre.kesql.orm.Model
import com.minek.kotlin.everywehre.kesql.orm.ModelTable
import com.minek.kotlin.everywehre.kesql.results.*
import com.minek.kotlin.everywehre.kesql.table.AbstractTableMetadata
import com.minek.kotlin.everywehre.kesql.table.Column
import java.util.*

class Query<R : Result>(public val identifiers: List<SelectIdentifier<*>>) : Filter<Query<R>> {
    override val where = WhereClause()

    val columns: List<Column<*>>
        get() {
            return identifiers.flatMap {
                when (it) {
                    is Column -> listOf(it)
                    is ModelTable -> it.meta.columns
                    else -> throw IllegalArgumentException()
                }
            }
        }

    val tables: List<AbstractTableMetadata>
        get() {
            return identifiers
                    .map {
                        when (it) {
                            is Column -> it.table.meta
                            is ModelTable -> it.meta
                            else -> throw IllegalArgumentException()
                        }
                    }
                    .toCollection(LinkedHashSet<AbstractTableMetadata>())
                    .toList()
        }


    public val sql: String
        get() {
            val tables = tables.map { quote(it.name) }.joinToString(", ")
            val fields = columns.map { quote(it.sql) }.joinToString()
            return listOf("SELECT $fields from $tables", where.sql).compact().joinToString(" ")
        }

    fun all(): List<R> {
        val database = tables.first().database
        val statement = database.session.connection.prepareStatement(sql)
        where.setParameters(statement, 0)
        val resultSet = statement.executeQuery()
        val results = arrayListOf<R>()
        while (resultSet.next()) {
            var index = 0
            val values = identifiers
                    .map { selectIdentifier ->
                        val pair = when (selectIdentifier) {
                            is Column ->
                                selectIdentifier to selectIdentifier.type.getValue(resultSet, ++index)
                            is ModelTable -> {
                                val model = selectIdentifier.meta.modelClass.newInstance() as Model
                                database.session.add(model)
                                selectIdentifier.meta.columns.forEach {
                                    model.metadata.set(it, it.type.getValue(resultSet, ++index), dirty = false)
                                }
                                model.metadata.isSynced = true
                                index += selectIdentifier.meta.columns.size
                                selectIdentifier to model
                            }
                            else -> throw IllegalArgumentException()
                        }
                        pair
                    }
                    .toMap()
            results.add(ResultImpl<Any, Any, Any>(values) as R)
        }
        return results
    }

    public fun first(): R? {
        return all().firstOrNull()
    }

    public companion object {
        public operator fun <T> invoke(identifier: SelectIdentifier<T>): Query<Result1<T>> =
                Query(listOf(identifier))

        public operator fun <T1, T2> invoke(identifier1: SelectIdentifier<T1>, identifier2: SelectIdentifier<T2>): Query<Result2<T1, T2>> =
                Query(listOf(identifier1, identifier2))

        public operator fun <T1, T2, T3> invoke(identifier1: SelectIdentifier<T1>, identifier2: SelectIdentifier<T2>, identifier3: SelectIdentifier<T3>): Query<Result3<T1, T2, T3>> =
                Query(listOf(identifier1, identifier2, identifier3))
    }
}
