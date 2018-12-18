package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName

class BookNowOfferEntity(
    @SerialName(TransportTypeEntity.TRANSPORT_TYPE_ID) val transportTypeId: String,
    @SerialName(AMOUNT) val amount: Double,
    @SerialName(BASE) val base: MoneyEntity,
    @SerialName(WITHOUT_DISCOUNT) val withoutDiscount: MoneyEntity?
) {

    companion object {
        const val AMOUNT           = "amount"
        const val BASE             = "base"
        const val WITHOUT_DISCOUNT = "without_discount"
    }
}
