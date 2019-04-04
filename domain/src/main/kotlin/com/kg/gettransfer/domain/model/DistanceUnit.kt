package com.kg.gettransfer.domain.model

/**
 * Kilometers & miles
 */
enum class DistanceUnit {
    km(), mi();

    companion object {
        val DEFAULT_DISTANCE_UNITS = arrayListOf(DistanceUnit.km, DistanceUnit.mi)
    }
}
