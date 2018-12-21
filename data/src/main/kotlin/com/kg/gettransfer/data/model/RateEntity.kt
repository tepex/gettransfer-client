package com.kg.gettransfer.data.model

class RateEntity(val offer_id: Long, val rate_type: String, val value: Float) {

    companion object {
        const val RATED_OFFER_ID = "offer_id"
        const val RATE_TYPE      = "type"
        const val TOTAL_RATING   = "value"
    }
}