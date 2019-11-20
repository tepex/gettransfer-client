package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.model.RouteModel

open class RouteMapper {
    fun getView(
        from: String,
        to: String?,
        fromPoint: Point,
        toPoint: Point,
        dateTime: String,
        distance: Int?,
        isRoundTrip: Boolean,
        polyLines: List<String>?
    ) =
    RouteModel(
        from,
        to,
        fromPoint,
        toPoint,
        dateTime,
        distance ?: Mapper.checkDistance(fromPoint, toPoint),
        isRoundTrip,
        polyLines
    )
}
