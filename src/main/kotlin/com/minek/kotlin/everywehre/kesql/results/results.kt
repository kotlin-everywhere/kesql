package com.minek.kotlin.everywehre.kesql.results

import com.minek.kotlin.everywehre.kesql.sql.SelectIdentifier

interface Result {
    fun getValue(index: Int): Any
    val values: List<Any>
}

interface Result1<out T> : Result {
    operator fun component1(): T {
        return getValue(0) as T
    }
}

interface Result2<out T1, out T2> : Result1<T1> {
    operator fun component2(): T2 {
        return getValue(1) as T2
    }
}

interface Result3<out T1, out T2, out T3> : Result2<T1, T2> {
    operator fun component3(): T3 {
        return getValue(2) as T3
    }
}

class ResultImpl<out T1, out T2, out T3>(map: Map<SelectIdentifier<*>, Any>) : Result3<T1, T2, T3> {
    private val entries = map.entries.toList()
    override val values: List<Any>
        get() = entries.map { it.value }

    override fun getValue(index: Int): Any {
        return entries[index].value
    }
}

