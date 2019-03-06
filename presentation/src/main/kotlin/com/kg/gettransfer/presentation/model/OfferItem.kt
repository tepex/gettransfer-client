package com.kg.gettransfer.presentation.model

sealed class OfferItem

data class BookNowOfferModel(
        val amount: Double,
        val base: MoneyModel,
        val withoutDiscount: MoneyModel?
) : OfferItem() {

    lateinit var transportType: TransportTypeModel
}

data class OfferModel(
        val id: Long,
        val transferId: Long,
        val status: String,
        val wifi: Boolean,
        val refreshments: Boolean,
        val charger: Boolean,
        val createdAt: String,
        val price: PriceModel,
        val ratings: RatingsModel?,
        val passengerFeedback: String?,
        val carrier: CarrierModel,
        val vehicle: VehicleModel,
        val driver: ProfileModel?,
        val phoneToCall: String?
) : OfferItem() {

    companion object {
        const val FULL_PRICE = 100
        const val PRICE_30   = 30
        const val PRICE_70   = 70
    }
}
