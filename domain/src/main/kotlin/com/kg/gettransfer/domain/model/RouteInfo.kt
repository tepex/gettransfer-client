package com.kg.gettransfer.domain.model

data class RouteInfo(
    val success: Boolean,
    val distance: Int?,
    val duration: Int?,
    val prices: List<TransportTypePrice>,
    val watertaxi: Boolean,
    val polyLines: List<String>,
    val overviewPolyline: String?
)

data class TransportTypePrice(
    val tranferId: String,
    val minFloat: Float,
    val min: String
)
