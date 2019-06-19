package com.kg.gettransfer.domain.model

data class RouteInfo(
    val success: Boolean,
    val distance: Int?,
    val duration: Int?,
    val prices: Map<TransportType.ID, TransportTypePrice>,
    val watertaxi: Boolean,
    val polyLines: List<String>,
    val overviewPolyline: String?,
    val hintsToComments: List<String>?
)

