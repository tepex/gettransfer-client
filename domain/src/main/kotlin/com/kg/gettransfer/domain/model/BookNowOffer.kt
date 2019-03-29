package com.kg.gettransfer.domain.model

import java.io.Serializable

data class BookNowOffer(
    val amount: Double,
    val base: Money,
    val withoutDiscount: Money?
): Serializable
