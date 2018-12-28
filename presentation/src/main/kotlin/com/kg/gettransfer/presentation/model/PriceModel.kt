package com.kg.gettransfer.presentation.model

data class PriceModel(
    val base: MoneyModel,
    val withoutDiscount: MoneyModel?,
    val percentage30: String,
    val percentage70: String,
    val amount: Double
)
