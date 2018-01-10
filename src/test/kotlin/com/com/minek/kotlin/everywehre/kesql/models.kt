package com.com.minek.kotlin.everywehre.kesql

import com.minek.kotlin.everywehre.kesql.Database
import com.minek.kotlin.everywehre.kesql.orm.Model
import com.minek.kotlin.everywehre.kesql.orm.ModelTable
import com.minek.kotlin.everywehre.kesql.table.Column
import com.minek.kotlin.everywehre.kesql.table.Table
import com.minek.kotlin.everywehre.kesql.types.DateType
import com.minek.kotlin.everywehre.kesql.types.IntType
import com.minek.kotlin.everywehre.kesql.types.StringType

object User : Table() {
    val id = Column(IntType(), primaryKey = true)
    val name = Column(StringType())
    val fullName = Column(StringType())
    val createdAt = Column(DateType())
}

object Empty : Table() {
    val id = Column(IntType(), primaryKey = true)
    val title = Column(StringType())
    val content = Column(StringType())
}

class Author : Model() {
    val id by Companion.id
    var name by Companion.name

    companion object : ModelTable<Author>() {
        val id = Column(IntType(), primaryKey = true)
        val name = Column(StringType())

        operator fun invoke(name: String = ""): Author {
            val author = Author()
            author.name = name
            return author
        }
    }
}

public val db: Database = object : Database() {
    init {
        addTables(listOf(User, Empty, Author))
    }
}
