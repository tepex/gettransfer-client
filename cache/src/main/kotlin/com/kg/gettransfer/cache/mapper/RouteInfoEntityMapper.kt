package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.RouteInfoCached
import com.kg.gettransfer.cache.model.StringList
import com.kg.gettransfer.data.model.RouteInfoEntity
import org.koin.standalone.KoinComponent

open class RouteInfoEntityMapper : KoinComponent {

    fun fromCached(type: RouteInfoCached) =
            RouteInfoEntity(
                    success          = type.success,
                    distance         = type.distance,
                    duration         = type.duration,
                    prices           = mapOf(),
                    watertaxi        = type.watertaxi,
                    polyLines        = type.polyLines.list,
                    overviewPolyline = type.overviewPolyline
            )

    fun toCached(from: String, to: String, type: RouteInfoEntity) =
            RouteInfoCached(
                    fromPoint        = from,
                    toPoint          = to,
                    success          = type.success,
                    distance         = type.distance,
                    duration         = type.duration,
                    watertaxi        = type.watertaxi,
                    polyLines        = StringList(type.polyLines),
                    overviewPolyline = type.overviewPolyline
            )
}