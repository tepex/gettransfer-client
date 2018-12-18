package com.kg.gettransfer.domain.model

data class TransportTypePrice(
    val transportTypeId: String,
    val minFloat: Float,
    val min: String,
    val bookNow: String?
)
