package com.minek.kotlin.everywehre.kesql.session

import com.minek.kotlin.everywehre.kesql.Database
import com.minek.kotlin.everywehre.kesql.orm.Model
import com.minek.kotlin.everywehre.kesql.sql.Delete
import com.minek.kotlin.everywehre.kesql.sql.Insert
import com.minek.kotlin.everywehre.kesql.sql.Update
import com.minek.kotlin.everywehre.kesql.table.Column
import org.postgresql.Driver
import java.sql.Connection
import java.sql.DriverManager

abstract class Session {
    abstract val connection: Connection

    abstract fun add(model: Model)

    abstract fun delete(vararg models: Model)

    abstract fun commit()

    abstract fun remove()
}


class ThreadSessionImpl(private val database: Database) : Session() {

    private val models = hashSetOf<Model>()
    private val deletes = hashSetOf<Model>()

    override val connection: Connection by lazy {
        DriverManager.registerDriver(Driver())
        val conn = DriverManager.getConnection(database.url)
        conn.autoCommit = false
        conn
    }

    override fun add(model: Model) {
        models.add(model)
    }

    override fun delete(vararg models: Model) {
        deletes.addAll(models)
    }

    private fun flush() {
        models.filter { !it.metadata.isSynced }
                .forEach {
                    val modelTable = database[it.metadata]
                    val pks: Map<Column<*>, Any> = Insert(modelTable)
                            .set(it.metadata.property.map { it.key to it.value })
                            .execute()
                    it.metadata.set(pks, dirty = false)
                    it.metadata.clearDirty()
                    it.metadata.isSynced = true
                }
        models.filter { it.metadata.dirtyColumns.isNotEmpty() }
                .forEach { model ->
                    val modelTable = database[model.metadata]
                    Update(modelTable)
                            .set(
                                    model.metadata.property.filter { it.key in model.metadata.dirtyColumns }
                            )
                            .filter(modelTable.meta.primaryKeyCondition(model))
                            .execute()
                    model.metadata.clearDirty()
                }
        deletes
                .forEach {
                    val modelTable = database[it.metadata]
                    Delete(modelTable).filter(modelTable.meta.primaryKeyCondition(it)).execute()
                }
        models.removeAll(deletes)
        deletes.removeAll(deletes.toList())
    }

    override fun commit() {
        flush()
        connection.commit()
    }

    override fun remove() {
        models.removeAll(models.toList())
        deletes.removeAll(deletes.toList())
        connection.rollback()
        connection.close()
    }
}

class ThreadSession(private val database: Database) : Session() {

    private val sessionPool = ThreadLocal<ThreadSessionImpl>()
    private val session: ThreadSessionImpl
        get() {
            var threadSessionImpl = sessionPool.get()
            if (threadSessionImpl == null) {
                threadSessionImpl = ThreadSessionImpl(database)
                sessionPool.set(threadSessionImpl)
            }
            return threadSessionImpl
        }
    override val connection: Connection
        get() = session.connection

    override fun add(model: Model) {
        session.add(model)
    }

    override fun delete(vararg models: Model) {
        session.delete(*models)
    }

    override fun commit() {
        session.commit()
    }

    override fun remove() {
        session.remove()
        sessionPool.remove()
    }
}