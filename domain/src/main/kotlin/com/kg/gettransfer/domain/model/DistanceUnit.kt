package com.kg.gettransfer.domain.model

import kotlin.math.roundToInt

/**
 * Kilometers & miles
 */
enum class DistanceUnit {
    km(), mi();

    companion object {
        fun km2Mi(km: Int) = (km.toDouble() / 1.609344).roundToInt()

        val DEFAULT_DISTANCE_UNITS = arrayListOf(DistanceUnit.km, DistanceUnit.mi)
    }
}
