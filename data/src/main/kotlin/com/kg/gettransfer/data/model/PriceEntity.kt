package com.kg.gettransfer.data.model

import java.util.Locale

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PriceEntity(
    @SerialName(BASE) val base: MoneyEntity,
    @Optional @SerialName(NO_DISCOUNT) val withoutDiscount: MoneyEntity? = null,
    @SerialName(PERCENTAGE_30) val percentage30: String,
    @SerialName(PERCENTAGE_70) val percentage70: String,
    @SerialName(AMOUNT) val amount: Double
) {

    companion object {
        const val BASE          = "base"
        const val NO_DISCOUNT   = "without_discount"
        const val PERCENTAGE_30 = "percentage_30"
        const val PERCENTAGE_70 = "percentage_70"
        const val AMOUNT        = "amount"
    }
}
