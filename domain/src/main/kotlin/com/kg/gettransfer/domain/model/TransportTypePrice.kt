package com.kg.gettransfer.domain.model

data class TransportTypePrice(
    val bookNow: TransportType.ID?,
    val minFloat: Float,
    val min: String
)
