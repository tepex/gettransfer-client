package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.TransportTypePrice

data class TransportTypePriceModel(
    val bookNow: TransportType.ID?,
    val minFloat: Float,
    val min: String
)

fun TransportTypePrice.map() = TransportTypePriceModel(bookNow, minFloat, min)
