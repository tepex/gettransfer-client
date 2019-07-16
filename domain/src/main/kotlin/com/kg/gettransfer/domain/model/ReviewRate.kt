package com.kg.gettransfer.domain.model

data class ReviewRate(
    val rateType: RateType,
    val rateValue: Int
) {

    enum class RateType {
        VEHICLE, DRIVER, COMMUNICATION
    }
}
