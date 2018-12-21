package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.TransportType

data class TransportTypePriceModel(
    val bookNow: TransportType.ID?,
    val minFloat: Float,
    val min: String
)
