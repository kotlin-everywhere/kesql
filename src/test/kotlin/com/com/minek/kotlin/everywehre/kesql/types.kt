package com.com.minek.kotlin.everywehre.kesql

import com.minek.kotlin.everywehre.kesql.sql.Query
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class TypeTest {
    @Test
    fun testInt() {
        Assert.assertEquals(setOf(1, 2), Query(User.id).all().map { it.component1() }.toSet())
    }

    @Test
    fun testString() {
        Assert.assertEquals(setOf("steve", "bill"), Query(User.name).all().map { it.component1() }.toSet())
    }

    @Test
    fun testDate() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        Assert.assertEquals(
                setOf("1955-2-24 09:09:09", "1955-10-28 23:23:23").map { simpleDateFormat.parse(it) }.toSet(),
                Query(User.createdAt).all().map { it.component1() }.toSet()
        )
    }

    @Test
    fun testUuid() {
        val uuid = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        Assert.assertEquals(
                setOf(uuid),
                Query(Types.uuid).filter(Types.uuid eq uuid).all().map { it.component1() }.toSet()
        )
    }
}

