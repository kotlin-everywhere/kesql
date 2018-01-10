package com.com.minek.kotlin.everywehre.kesql.orm

import com.com.minek.kotlin.everywehre.kesql.Author
import com.com.minek.kotlin.everywehre.kesql.BaseTest
import com.com.minek.kotlin.everywehre.kesql.db
import com.minek.kotlin.everywehre.kesql.sql.Query
import org.junit.Assert
import org.junit.Test

class UpdateTest : BaseTest() {
    @Test
    fun testUpdate() {
        val author = Query(Author).filter(Author.id eq 1).first()!!.component1()
        Assert.assertEquals("Stephen King", author.name)
        author.name = "Richard Bachman"
        db.session.commit()

        val authors = Query(Author.id, Author.name).all().sortedBy { it.component1() }
        Assert.assertEquals(2, authors.size)
        val (bachmanId, bachmanName) = authors[0]
        Assert.assertEquals(1, bachmanId)
        Assert.assertEquals("Richard Bachman", bachmanName)
        val (ecoId, ecoName) = authors[1]
        Assert.assertEquals(2, ecoId)
        Assert.assertEquals("Umberto Eco", ecoName)
    }
}
