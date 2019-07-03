package com.kg.gettransfer.core.domain

inline class Hour(val hours: Int) {

    val minutes: Minute
        get() = Minute(hours * MINUTES_PER_HOUR)

    companion object {
        const val MINUTES_PER_HOUR = 60
    }
}
