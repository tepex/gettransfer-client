package com.kg.gettransfer.domain.model

data class Price(
    val base: Money,
    val withoutDiscount: Money?,
    val percentage30: String,
    val percentage70: String,
    val amount: Double // Double !!!
)
