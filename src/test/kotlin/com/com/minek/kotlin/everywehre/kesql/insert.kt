package com.com.minek.kotlin.everywehre.kesql

import com.minek.kotlin.everywehre.kesql.sql.Insert
import org.junit.Assert
import org.junit.Test

class InsertTest : BaseTest() {
    @Test
    fun testInsertValues() {
        val insert = Insert(User)
        Assert.assertEquals("INSERT INTO \"user\" VALUES DEFAULT", insert.sql)

        insert.set(User.name, "steve")
        Assert.assertEquals("INSERT INTO \"user\" (name) VALUES (?)", insert.sql)

        insert.set(User.fullName, "Steve Jobs")
        Assert.assertEquals("INSERT INTO \"user\" (name, full_name) VALUES (?, ?)", insert.sql)
    }

    @Test
    fun testInsertUser() {
        val primaryKeys = Insert(Empty).set(Empty.title, "The title").set(Empty.content, "content").execute()
        Assert.assertEquals(1, primaryKeys.size)
        Assert.assertEquals(1, primaryKeys[Empty.id])
    }
}