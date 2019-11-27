package com.kg.gettransfer.domain.model

data class Price(
    val base: Money,
    val withoutDiscount: Money?,
    val amount: Double // Double !!!
)
