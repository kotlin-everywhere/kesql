package com.com.minek.kotlin.everywehre.kesql

import com.minek.kotlin.everywehre.kesql.sql.Query
import com.minek.kotlin.everywehre.kesql.sql.Delete
import org.junit.Assert
import org.junit.Test

class DeleteTest : BaseTest() {
    @Test
    fun testSQL() {
        Assert.assertEquals("DELETE FROM \"user\"", Delete(User).sql)
    }

    @Test
    fun testFilter() {
        Assert.assertEquals("DELETE FROM \"user\" WHERE \"user\".id = ?", Delete(User).filter(User.id eq 1).sql)
    }

    @Test
    fun testExecute() {
        Delete(User).execute()
        Assert.assertTrue(Query(User.id).all().isEmpty())
    }

    @Test
    fun testExecuteFilter() {
        Delete(User).filter(User.id eq 1).execute()
        Assert.assertEquals(
                listOf(listOf(2, "bill", "Bill Gates")), Query(User.id, User.name, User.fullName).all().map { it.values }
        )
    }
}
