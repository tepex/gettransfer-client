package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.ReviewRate

data class ReviewRateEntity(
    val rateType: String,
    val value: Int
) {

    companion object {
        const val RATED_OFFER_ID = "offer_id"
        const val RATE_TYPE      = "type"
        const val TOTAL_RATING   = "value"
    }
}

fun ReviewRate.map() = ReviewRateEntity(rateType.type, rateValue)
