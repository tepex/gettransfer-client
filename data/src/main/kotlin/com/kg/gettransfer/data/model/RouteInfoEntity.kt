package com.kg.gettransfer.data.model

data class RouteInfoEntity(
    val success: Boolean,
    val distance: Int?,
    val duration: Int?,
    val prices: List<TransportTypePriceEntity>,
    val watertaxi: Boolean,
    val polyLines: List<String>,
    val overviewPolyline: String?
) {

    companion object {
        const val SUCCESS   = "success"
        const val DISTANCE  = "distance"
        const val DURATION  = "duration"
        const val PRICES    = "prices"
        const val WATERTAXI = "watertaxi"
        const val ROUTES    = "routes"
    }
}
