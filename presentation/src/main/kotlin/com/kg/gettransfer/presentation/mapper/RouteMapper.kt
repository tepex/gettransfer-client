package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.model.RouteModel

open class RouteMapper {
    fun getView(
        distance: Int?,
        polyLines: List<String>?,
        from: String,
        to: String,
        fromPoint: Point,
        toPoint: Point,
        dateTime: String
    ) =
    RouteModel(
        distance ?: Mapper.checkDistance(fromPoint, toPoint),
        polyLines,
        from,
        to,
        fromPoint,
        toPoint,
        dateTime
    )
}
