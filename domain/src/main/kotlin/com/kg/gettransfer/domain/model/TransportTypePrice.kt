package com.kg.gettransfer.domain.model

data class TransportTypePrice(
    val minFloat: Float,
    val min: String,
    val bookNow: TransportType.ID?
)
