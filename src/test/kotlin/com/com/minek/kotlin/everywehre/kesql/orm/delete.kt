package com.com.minek.kotlin.everywehre.kesql.orm

import com.com.minek.kotlin.everywehre.kesql.Author
import com.com.minek.kotlin.everywehre.kesql.BaseTest
import com.com.minek.kotlin.everywehre.kesql.db
import com.minek.kotlin.everywehre.kesql.sql.Query
import org.junit.Assert
import org.junit.Test

class DeleteTest : BaseTest() {
    @Test
    fun testDelete() {
        val author = Query(Author).filter(Author.id eq 1).first()!!.component1()
        db.session.delete(author)
        db.session.commit()

        Assert.assertEquals(listOf(2), Query(Author.id).all().map { it.component1() }.sortedBy { it })
    }
}
