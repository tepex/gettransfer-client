package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.TransportType

data class RouteInfoEntity(
    val success: Boolean,
    val distance: Int?,
    val duration: Int?,
    val prices: Map<String, TransportTypePriceEntity>,
    val watertaxi: Boolean,
    val polyLines: List<String>,
    val overviewPolyline: String?,
    val hintsToComments: List<String>?
) {

    companion object {
        const val ENTITY_NAME           = "route_info"
        const val SUCCESS               = "success"
        const val DISTANCE              = "distance"
        const val DURATION              = "duration"
        const val PRICES                = "prices"
        const val WATERTAXI             = "watertaxi"
        const val ROUTES                = "routes"
        const val POLYLINES             = "polylines"
        const val OVERVIEW_POLYLINE     = "overview_polyline"
        const val FROM_POINT            = "from_point"
        const val TO_POINT              = "to_point"
        const val HINTS_TO_COMMENTS     = "hints_to_comments"
    }
}

fun RouteInfoEntity.map() =
    RouteInfo(
        success,
        distance,
        duration,
        prices.entries.associate { it.key.map() to it.value.map() },
        watertaxi,
        polyLines,
        overviewPolyline,
        hintsToComments
    )
