package com.kg.gettransfer.extensions

fun String.getShortAddress() =
    substring(0, MAX_CHARS).substringBefore(COMMA)


const val MAX_CHARS = 12
const val COMMA    = ","