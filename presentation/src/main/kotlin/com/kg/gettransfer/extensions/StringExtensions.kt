package com.kg.gettransfer.extensions

fun String.getShortAddress() =
        substringBefore(COMMA)
                .let {
                    if (it.length > MAX_CHARS)
                            it.substring(0, MAX_CHARS - 1)
                    else return@let it
                }

const val MAX_CHARS = 12
const val COMMA    = ","