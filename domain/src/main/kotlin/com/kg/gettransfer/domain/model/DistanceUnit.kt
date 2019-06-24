package com.kg.gettransfer.domain.model

/**
 * Kilometers & miles
 */
enum class DistanceUnit {
    KM(), MI();

    companion object {
        val DEFAULT_DISTANCE_UNITS = arrayListOf(DistanceUnit.KM, DistanceUnit.MI)
    }
}
