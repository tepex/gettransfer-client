package com.kg.gettransfer.domain.model

data class BookNowOffer(
    val amount: Double,
    val base: Money,
    val withoutDiscount: Money?
)
