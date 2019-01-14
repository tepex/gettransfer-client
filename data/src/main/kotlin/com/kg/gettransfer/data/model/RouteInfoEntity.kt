package com.kg.gettransfer.data.model

data class RouteInfoEntity(
    val success: Boolean,
    val distance: Int?,
    val duration: Int?,
    val prices: Map<String, TransportTypePriceEntity>,
    val watertaxi: Boolean,
    val polyLines: List<String>,
    val overviewPolyline: String?
) {

    companion object {
        const val ENTITY_NAME       = "route_info"
        const val SUCCESS           = "success"
        const val DISTANCE          = "distance"
        const val DURATION          = "duration"
        const val PRICES            = "prices"
        const val WATERTAXI         = "watertaxi"
        const val ROUTES            = "routes"
        const val POLYLINES         = "polylines"
        const val OVERVIEW_POLYLINE = "overview_polyline"
        const val FROM_POINT        = "from_point"
        const val TO_POINT          = "to_point"
    }
}
