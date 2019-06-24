package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.TransportType

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
class BookNowOfferEntity(
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

fun BookNowOfferEntity.map(transportType: TransportType) =
    BookNowOffer(
        amount,
        base.map(),
        withoutDiscount?.map(),
        transportType
    )
