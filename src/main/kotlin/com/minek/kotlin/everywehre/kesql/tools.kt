package com.minek.kotlin.everywehre.kesql

fun variableName(className: String): String {
    return (className.substring(0, 1).toLowerCase() + className.substring(1)).underscored()
}

fun Collection<String>.compact(): List<String> {
    return this.filter { it.isNotEmpty() }
}

fun String.underscored(): String {
    return replace(Regex("[A-Z]")) { "_" + it.value.toLowerCase() }
}
