package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Price
import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceEntity(
    @SerialName(BASE) val base: MoneyEntity,
    @Optional @SerialName(NO_DISCOUNT) val withoutDiscount: MoneyEntity? = null,
    @SerialName(AMOUNT) val amount: Double
) {

    companion object {
        const val BASE        = "base"
        const val NO_DISCOUNT = "without_discount"
        const val AMOUNT      = "amount"
    }
}

fun Price.map() =
    PriceEntity(
        base.map(),
        withoutDiscount?.map(),
        amount
    )

fun PriceEntity.map() =
    Price(
        base.map(),
        withoutDiscount?.map(),
        amount
    )
