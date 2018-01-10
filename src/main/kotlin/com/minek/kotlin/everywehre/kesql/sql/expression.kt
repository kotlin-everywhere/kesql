package com.minek.kotlin.everywehre.kesql.sql

import com.minek.kotlin.everywehre.kesql.table.AbstractTable
import com.minek.kotlin.everywehre.kesql.table.Column
import java.sql.PreparedStatement
import java.util.LinkedHashMap


class Insert(private val table: AbstractTable) {
    private val values = linkedMapOf<Column<*>, Any>()
    val sql: String
        get() {
            val prefix = "INSERT INTO ${quote(table.meta.name)}"
            val postfix =
                    if (values.isEmpty()) {
                        "VALUES DEFAULT"
                    } else {
                        val names = values.map { it.key.name }
                        "(${names.map { quote(it) }.joinToString()}) VALUES (${names.map { "?" }.joinToString()})"
                    }
            return "$prefix $postfix"
        }

    fun <T: Any> set(column: Column<T>, value: T): Insert {
        values.set(column, value)
        return this
    }

    fun set(pairs: List<Pair<Column<*>, Any>>): Insert {
        pairs.forEach { values.set(it.first, it.second) }
        return this
    }

    fun execute(): Map<Column<*>, Any> {
        val query = "$sql RETURNING ${table.meta.primaryKeys.map { it.name }.joinToString()}"
        val statement = table.meta.database.session.connection.prepareStatement(query)
        values.entries.forEachIndexed { i, entry ->
            entry.key.type.setValue(statement, i + 1, entry.value)
        }
        val resultSet = statement.executeQuery()
        resultSet.next()
        return table.meta.primaryKeys
                .mapIndexed { i, column ->
                    column to column.type.getValue(resultSet, i + 1)
                }
                .toMap()
    }
}

interface Filter<T> {
    val where: WhereClause

    fun filter(vararg conditions: Condition): T {
        return filter(conditions.toList())
    }

    fun filter(conditions: Collection<Condition>): T {
        where.filter(conditions)
        return this as T
    }
}


class Update(val table: AbstractTable) : Filter<Update> {
    val values: Map<Column<*>, Any> = linkedMapOf()
    override val where = WhereClause()

    val sql: String
        get() {
            val sets = values.map { "${it.key.quoteName} = ?" }.joinToString()

            return arrayOf("UPDATE ${table.meta.quoteName} SET $sets", where.sql)
                    .filter { it.isNotEmpty() }
                    .joinToString(" ")
        }

    fun <T: Any> set(column: Column<T>, value: T): Update {
        (values as LinkedHashMap).set(column, value)
        return this
    }

    fun set(map: Map<Column<*>, Any>): Update {
        map.forEach { (values as LinkedHashMap).set(it.key, it.value) }
        return this
    }

    fun execute(echo: Boolean = false) {
        if (echo) println(sql)
        val statement = table.meta.database.session.connection.prepareStatement(sql)
        values.entries.forEachIndexed { i, entry ->
            entry.key.type.setValue(statement, i + 1, entry.value)
        }
        where.setParameters(statement, values.entries.size)
        statement.execute()
    }

}


class Delete(val table: AbstractTable): Filter<Delete> {
    override val where = WhereClause()

    val sql: String
        get() {
            return arrayOf("DELETE FROM ${table.meta.quoteName}", where.sql).filter { it.isNotEmpty() }.joinToString(" ")
        }

    fun execute() {
        val statement = table.meta.database.session.connection.prepareStatement(sql)
        where.setParameters(statement, 0)
        statement.execute()
    }
}

interface Identifier {
    val sql: String
}

class WhereClause {
    private var condition: Condition = EmptyCondition()

    val sql: String
        get() = if (condition.isEmpty()) "" else "WHERE ${condition.sql}"

    fun filter(conditions: Collection<Condition>) {
        condition = and_(condition, and_(conditions))
    }

    fun setParameters(statement: PreparedStatement, beginIndex: Int) {
        condition.parameters.forEachIndexed { i, identifier ->
            identifier.setParameter(statement, beginIndex + i + 1)
        }
    }
}


interface SelectIdentifier<T> : Identifier
