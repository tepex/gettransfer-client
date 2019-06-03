package com.kg.gettransfer.presentation.mapper

import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.model.Location

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.presentation.model.PointModel

open class PointMapper : Mapper<PointModel, Point> {
    fun toLatLng(type: Point) = LatLng(type.latitude, type.longitude)
    fun fromLatLng(type: LatLng) = Point(type.latitude, type.longitude)

    override fun fromView(type: PointModel) = Point(type.latitude, type.longitude)
    override fun toView(type: Point) = PointModel(type.latitude, type.longitude)

    fun fromLocationToLatLng(type: Location) = LatLng(type.latitude, type.longitude)
    fun toLocation(type: Point) = Location(type.latitude, type.longitude)

}
