package com.kg.gettransfer.presentation.model

class OfferModel(
    val id: Long,
    val status: String,
    val wifi: Boolean,
    val refreshments: Boolean,
    val createdAt: String,
    val price: PriceModel,
    val ratings: RatingsModel?,
    val passengerFeedback: String?,
    val carrier: CarrierModel,
    val vehicle: VehicleModel,
    val driver: ProfileModel?,
    val phoneToCall: String?
) {

    companion object {
        const val FULL_PRICE = 100
        const val PRICE_30   = 30
        const val PRICE_70   = 70
    }
}
