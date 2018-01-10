package com.com.minek.kotlin.everywehre.kesql

import com.minek.kotlin.everywehre.kesql.sql.Query
import com.minek.kotlin.everywehre.kesql.sql.Update
import org.junit.Assert
import org.junit.Test

class UpdateTest : BaseTest() {
    @Test
    fun testUpdate() {
        Assert.assertEquals(
                "UPDATE \"user\" SET name = ?, full_name = ?",
                Update(User).set(User.name, "tim").set(User.fullName, "Tim Cook").sql
        )
    }

    @Test
    fun testExecute() {
        Update(User).set(User.name, "tim").set(User.fullName, "Tim Cook").execute()
        Assert.assertEquals(
                listOf(listOf("tim", "Tim Cook"), listOf("tim", "Tim Cook")),
                Query(User.name, User.fullName).all().map { it.values }
        )
    }

    @Test
    fun testFilter() {
        Assert.assertEquals(
                "UPDATE \"user\" SET name = ?, full_name = ? WHERE \"user\".id = ?",
                Update(User).set(User.name, "tim").set(User.fullName, "Tim Cook").filter(User.id eq 1).sql
        )
    }

    @Test
    fun testExecuteWithFilter() {
        Update(User).set(User.name, "tim").set(User.fullName, "Tim Cook").filter(User.id eq 1).execute()
        Assert.assertEquals(
                listOf(listOf(1, "tim", "Tim Cook"), listOf(2, "bill", "Bill Gates")),
                Query(User.id, User.name, User.fullName).all().sortedBy { it.component1() }.map { it.values }
        )
    }
}
