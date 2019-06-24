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
) {

    companion object {
        val EMPTY = RouteInfo(
            success          = false,
            distance         = null,
            duration         = null,
            prices           = emptyMap(),
            watertaxi        = false,
            polyLines        = emptyList(),
            overviewPolyline = null,
            hintsToComments = emptyList()
        )
    }
}
