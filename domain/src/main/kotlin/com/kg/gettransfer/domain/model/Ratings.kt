package com.kg.gettransfer.domain.model

data class Ratings(
    val average: Float?,
    val vehicle: Float?,
    val driver: Float?,
    val fair: Float?
) {

    val vehicleNn = vehicle ?: 0f
    val driverNn = driver ?: 0f
    val fairNn = fair ?: 0f

    val averageRating: Float
        get() = average ?: (vehicleNn + driverNn + fairNn) / 3
}
