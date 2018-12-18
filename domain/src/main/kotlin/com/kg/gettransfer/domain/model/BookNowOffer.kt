package com.kg.gettransfer.domain.model

data class BookNowOffer(
    val transportTypeId: String,
    val amount: Double,
    val base: Money,
    val withoutDiscount: Money?
)
