package com.kg.gettransfer.core.domain

inline class Minute(val minutes: Int) {

    val seconds: Second
        get() = Second(SECONDS_PER_MINUTE * minutes)
    val hours: Hour
        get() = Hour(minutes / Hour.MINUTES_PER_HOUR)

    companion object {
        const val SECONDS_PER_MINUTE = 60
    }
}
