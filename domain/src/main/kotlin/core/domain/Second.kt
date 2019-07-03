package com.kg.gettransfer.core.domain

inline class Second(val seconds: Int) {

    val millis: Long
        get() = seconds * MILLIS_PER_SECOND

    companion object {
        const val MILLIS_PER_SECOND = 1_000L
    }
}
