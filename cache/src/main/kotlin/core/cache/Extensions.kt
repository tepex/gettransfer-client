package com.kg.gettransfer.core.cache

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
