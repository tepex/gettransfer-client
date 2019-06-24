package com.kg.gettransfer.domain.model

data class Ratings(
    private val hiddenAverage: Double?,
    val vehicle: Double,
    val driver: Double,
    val fair: Double
) {

    val average: Double
        get() = hiddenAverage ?: arrayOf(vehicle, driver, fair).average()

    companion object {
        const val NO_RATE = 0.0
    }
}
