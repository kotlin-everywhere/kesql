package com.com.minek.kotlin.everywehre.kesql.orm

import com.com.minek.kotlin.everywehre.kesql.Author
import com.com.minek.kotlin.everywehre.kesql.BaseTest
import com.minek.kotlin.everywehre.kesql.results.Result3
import com.minek.kotlin.everywehre.kesql.sql.Query
import org.junit.Assert
import org.junit.Test

class QueryTest : BaseTest() {
    @Test
    fun testQuery() {
        val authors: List<Author> = Query(Author).all().map { it.component1() }.sortedBy { it.id }

        val king = authors[0]
        Assert.assertEquals(1, king.id)
        Assert.assertEquals("Stephen King", king.name)

        val eco = authors[1]
        Assert.assertEquals(2, eco.id)
        Assert.assertEquals("Umberto Eco", eco.name)
    }

    @Test
    fun testQueryCombined() {
        val authors: List<Result3<Int, String, Author>> =
                Query(Author.id, Author.name, Author).all().sortedBy { it.component1() }

        val (kingId, kingName, king) = authors[0]
        Assert.assertEquals(1, kingId)
        Assert.assertEquals("Stephen King", kingName)
        Assert.assertEquals(1, king.id)
        Assert.assertEquals("Stephen King", king.name)

        val (ecoId, ecoName, eco) = authors[1]
        Assert.assertEquals(2, ecoId)
        Assert.assertEquals("Umberto Eco", ecoName)
        Assert.assertEquals(2, eco.id)
        Assert.assertEquals("Umberto Eco", eco.name)
    }
}
