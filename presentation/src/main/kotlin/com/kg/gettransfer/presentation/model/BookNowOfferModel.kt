package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.TransportType

data class BookNowOfferModel(
    val amount: Double,
    val base: MoneyModel,
    val withoutDiscount: MoneyModel?
) {
    lateinit var transportTypeId: TransportType.ID
}
