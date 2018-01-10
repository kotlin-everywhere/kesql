package com.minek.kotlin.everywehre.kesql.sql

import com.minek.kotlin.everywehre.kesql.types.ValueIdentifier

abstract class Condition {
    abstract val sql: String
    abstract val parameters: List<ValueIdentifier>
    open fun isEmpty(): Boolean {
        return false
    }

    infix fun or(right: Condition): Condition {
        return OrCondition(this, right)
    }
}

class EmptyCondition : Condition() {
    override val parameters: List<ValueIdentifier>
        get() = listOf()

    override val sql: String
        get() = ""

    override fun isEmpty(): Boolean {
        return true
    }
}

abstract class BinaryCondition(val left: Identifier, val right: Identifier) : Condition()

class EqualCondition(left: Identifier, right: Identifier) : BinaryCondition(left, right) {
    override val parameters: List<ValueIdentifier>
        get() = listOf(left, right).filterIsInstance(ValueIdentifier::class.java)

    override val sql: String
        get() = "${left.sql} = ${right.sql}"
}

fun and_(vararg conditions: Condition): Condition {
    return and_(conditions.toList())
}

fun and_(conditions: Collection<Condition>): Condition {
    return if (conditions.isEmpty()) {
        EmptyCondition()
    } else {
        conditions.reduce { left, right ->
            if (left.isEmpty()) {
                return right
            }
            if (right.isEmpty()) {
                return left
            }
            AndCondition(left, right)
        }
    }
}

class AndCondition(private val left: Condition, private val right: Condition) : Condition() {
    override val parameters: List<ValueIdentifier>
        get() = listOf(left, right).flatMap { it.parameters }

    override val sql: String
        get() = "$left AND $right"
}

class OrCondition(private val left: Condition, private val right: Condition) : Condition() {
    override val parameters: List<ValueIdentifier>
        get() = listOf(left, right).flatMap { it.parameters }

    override val sql: String
        get() = "(${left.sql}) OR (${right.sql})"
}
