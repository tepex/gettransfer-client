package com.kg.gettransfer.data.model

data class OfferFeedbackEntity(
    val offerId: Long,
    val comment: String
) {
    companion object {
        const val ENTITY_NAME = "user_feedback"
        const val OFFER_ID    = "offer_id"
        const val COMMENT     = "comment"
    }
}