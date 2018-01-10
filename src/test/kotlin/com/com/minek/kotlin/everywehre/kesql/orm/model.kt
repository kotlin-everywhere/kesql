package com.com.minek.kotlin.everywehre.kesql.orm

import com.com.minek.kotlin.everywehre.kesql.Author
import com.com.minek.kotlin.everywehre.kesql.BaseTest
import org.junit.Assert
import org.junit.Test

class ModelTest : BaseTest() {
    @Test
    fun testModel() {
        Assert.assertEquals("author", Author.meta.name)
    }

    @Test
    fun testColumn() {
        Assert.assertEquals(listOf(Author.id, Author.name), Author.meta.columns)
    }
}
