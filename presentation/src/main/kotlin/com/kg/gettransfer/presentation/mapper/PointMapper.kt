package com.kg.gettransfer.presentation.mapper

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.domain.model.Point

open class PointMapper : Mapper<LatLng, Point> {
    override fun toView(type: Point) = LatLng(type.latitude, type.longitude)
    override fun fromView(type: LatLng) = Point(type.latitude, type.longitude)
}
