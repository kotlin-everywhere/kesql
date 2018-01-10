package com.com.minek.kotlin.everywehre.kesql

import org.junit.Assert
import org.junit.Test

class TableTest : BaseTest() {
    @Test
    fun testName() {
        Assert.assertEquals("user", User.meta.name)
    }

    @Test
    fun testColumns() {
        Assert.assertEquals(
                listOf(User.id, User.name, User.fullName, User.createdAt), User.meta.columns
        )
    }
}


class ColumnTest : BaseTest() {
    @Test
    fun testName() {
        Assert.assertEquals(
                listOf("id", "name", "full_name", "created_at"),
                listOf(User.id.name, User.name.name, User.fullName.name, User.createdAt.name)
        )
    }
}