package com.minek.kotlin.everywehre.kesql.types

import com.minek.kotlin.everywehre.kesql.sql.Identifier
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*

abstract class ValueIdentifier : Identifier {
    override val sql: String
        get() = "?"

    abstract fun setParameter(statement: PreparedStatement, index: Int)
}

abstract class ColumnType<T> {
    abstract fun setValue(statement: PreparedStatement, index: Int, value: Any)

    abstract fun getValue(resultSet: ResultSet, i: Int): Any

    fun getIdentifier(value: Any): ValueIdentifier {
        return object : ValueIdentifier() {
            override fun setParameter(statement: PreparedStatement, index: Int) {
                setValue(statement, index, value)
            }
        }
    }
}

class IntType : ColumnType<Int>() {
    override fun getValue(resultSet: ResultSet, i: Int): Any {
        return resultSet.getInt(i)
    }

    override fun setValue(statement: PreparedStatement, index: Int, value: Any) {
        statement.setInt(index, value as Int)
    }
}

class StringType : ColumnType<String>() {
    override fun getValue(resultSet: ResultSet, i: Int): Any {
        return resultSet.getString(i)
    }

    override fun setValue(statement: PreparedStatement, index: Int, value: Any) {
        statement.setString(index, value as String)
    }
}

class DateType : ColumnType<Date>() {
    override fun setValue(statement: PreparedStatement, index: Int, value: Any) {
        statement.setTimestamp(index, Timestamp((value as Date).time))
    }

    override fun getValue(resultSet: ResultSet, i: Int): Any {
        return Date(resultSet.getTimestamp(i).time)
    }
}

class UuidType : ColumnType<UUID>() {
    override fun setValue(statement: PreparedStatement, index: Int, value: Any) {
        statement.setObject(index, value)
    }

    override fun getValue(resultSet: ResultSet, i: Int): Any {
        return resultSet.getObject(i, UUID::class.java)
    }
}