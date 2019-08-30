package com.kg.gettransfer.core.cache

fun Int.toBoolean() = if (this == 1) true else false

fun Boolean.toInt() = if (this) 1 else 0

fun Boolean?.toTernary(): Int = when (this) {
    null  -> 0
    true  -> 1
    false -> -1
}

fun Int.toTernary(): Boolean? = when (this) {
    -1   -> false
    1    -> true
    else -> null
}
