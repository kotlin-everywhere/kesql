package com.com.minek.kotlin.everywehre.kesql

import org.junit.After
import org.junit.Before

abstract class BaseTest {
    @Before
    fun before() {
        db.initialize("jdbc:postgresql:kesql")
        this.javaClass.getResourceAsStream("/create.sql").reader().readText().split(";").map {
            db.session.connection.createStatement().execute(it)
        }
        db.session.connection.commit()
    }

    @After
    fun after() {
        db.session.remove()
    }
}