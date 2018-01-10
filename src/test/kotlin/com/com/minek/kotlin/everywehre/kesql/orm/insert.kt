package com.com.minek.kotlin.everywehre.kesql.orm

import com.com.minek.kotlin.everywehre.kesql.Author
import com.com.minek.kotlin.everywehre.kesql.BaseTest
import com.com.minek.kotlin.everywehre.kesql.db
import com.minek.kotlin.everywehre.kesql.sql.Query
import org.junit.Assert
import org.junit.Test

class InsertTest : BaseTest() {
    @Test
    fun testInsert() {
        val author = Author(name = "William Shakespeare")
        db.session.add(author)
        db.session.commit()

        val (id, name) = Query(Author.id, Author.name).filter(Author.id eq 3).all().first()
        Assert.assertEquals(3, id)
        Assert.assertEquals("William Shakespeare", name)

        Assert.assertEquals(3, author.id)
        Assert.assertEquals("William Shakespeare", author.name)
    }
}
