package com.kg.gettransfer.data.model

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
