package com.minek.kotlin.everywehre.kesql.sql

private val reserveWords: Set<String> = setOf("user")

fun quote(identifier: String): String {
    if (identifier in reserveWords) {
        return "\"$identifier\""
    }
    return identifier
}
