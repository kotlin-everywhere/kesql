package com.com.minek.kotlin.everywehre.kesql

import com.minek.kotlin.everywehre.kesql.results.Result1
import com.minek.kotlin.everywehre.kesql.results.Result2
import com.minek.kotlin.everywehre.kesql.sql.Query
import org.junit.Assert
import org.junit.Test

class QueryTest : BaseTest() {
    @Test
    fun testQuerySQL() {
        Assert.assertEquals("SELECT \"user\".id from \"user\"", Query(User.id).sql)
    }

    @Test
    fun testQuery1() {
        val ids: List<Result1<Int>> = Query(User.id).all()
        Assert.assertEquals(
                listOf(1, 2), ids.map { it.component1() }.sortedBy { it }
        )

        Assert.assertEquals(
                listOf("bill", "steve"),
                Query(User.name).all().map { it.component1() }.sortedBy { it }
        )
    }

    @Test
    fun testQuery2() {
        val list: List<Result2<Int, String>> = Query(User.id, User.name).all().sortedBy { it.component1() }
        Assert.assertEquals(2, list.size)

        val (steveId: Int, steveName: String) = list[0]
        Assert.assertEquals(steveId, 1)
        Assert.assertEquals(steveName, "steve")

        val (billId: Int, billName: String) = list[1]
        Assert.assertEquals(billId, 2)
        Assert.assertEquals(billName, "bill")
    }

    @Test
    fun testQuery3() {
        val list: List<List<Any>> = Query(User.id, User.name, User.fullName)
                .all()
                .sortedBy { it.component1() }
                .map { it.values }

        Assert.assertEquals(
                listOf(listOf(1, "steve", "Steve Jobs"), listOf(2, "bill", "Bill Gates")),
                list
        )
    }

    @Test
    fun testFilter() {
        val query = Query(User.id).filter(User.id eq 2)
        Assert.assertEquals("SELECT \"user\".id from \"user\" WHERE \"user\".id = ?", query.sql)

        Assert.assertEquals(listOf(2), query.all().map { it.component1() })
    }
}
