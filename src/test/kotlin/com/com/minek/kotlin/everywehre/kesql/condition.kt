package com.com.minek.kotlin.everywehre.kesql

import com.minek.kotlin.everywehre.kesql.sql.Query
import org.junit.Assert
import org.junit.Test

class ConditionTest : BaseTest() {
    @Test
    fun testOr() {
        val condition = (User.id eq 1) or (User.name eq "bill")
        Assert.assertEquals("(\"user\".id = ?) OR (\"user\".name = ?)", condition.sql)

        val users = Query(User.id, User.name).filter(condition).all().sortedBy { it.component1() }
        Assert.assertEquals(2, users.size)
        val (steveId, steveName) = users[0]
        Assert.assertEquals(1, steveId)
        Assert.assertEquals("steve", steveName)
        val (billId, billName) = users[1]
        Assert.assertEquals(2, billId)
        Assert.assertEquals("bill", billName)
    }
}
