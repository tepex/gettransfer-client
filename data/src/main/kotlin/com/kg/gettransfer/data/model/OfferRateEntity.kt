package com.kg.gettransfer.data.model

data class OfferRateEntity(
    val id: Long,
    val offerId: Long,
    val reviewRate: ReviewRateEntity
) {
    companion object {
        const val ENTITY_NAME = "user_review_rate"
        const val ID          = "rate_id"
        const val OFFER_ID    = "rate_offer_id"
        const val REVIEW_RATE = "review_rate"
    }
}