package com.kg.gettransfer.extensions

fun Int.ceil(x: Int) = (this + x - 1) / x
fun Int.isNonZero() = if (this == 0) null else this
