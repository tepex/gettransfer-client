package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.domain.model.Point

/**
 * Map a [String] to and from a [Point] instance when data is moving between this later and the Domain layer.
 */
open class PointMapper : Mapper<String, Point> {
    companion object {
        private val POINT_REGEX = "\\(([\\d\\.\\-]+)\\,([\\d\\.\\-]+)\\)".toRegex()
    }

    override fun fromEntity(type: String): Point {
        val latLng = POINT_REGEX.find(type)!!.groupValues
        return Point(latLng.get(1).toDouble(), latLng.get(2).toDouble())
    }

    override fun toEntity(type: Point) = type.toString()
}
