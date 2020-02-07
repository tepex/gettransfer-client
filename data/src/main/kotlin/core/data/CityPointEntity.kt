package com.kg.gettransfer.core.data

import com.kg.gettransfer.core.domain.CityPoint
import com.kg.gettransfer.core.domain.Point

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityPointEntity(
    @SerialName(NAME) val name: String?,
    @SerialName(POINT) val point: String?,
    @SerialName(PLACE_ID) val placeId: String?
) {

    fun mapPoint(): Point? = point?.let { p ->
        POINT_REGEX.find(p)?.groupValues?.let { Point(it[1].toDouble(), it[2].toDouble()) }
    }

    companion object {
        const val NAME     = "name"
        const val POINT    = "point"
        const val PLACE_ID = "place_id"

        private val POINT_REGEX = "\\(([\\d.\\-]+),([\\d.\\-]+)\\)".toRegex()
    }
}

fun CityPoint.map() = CityPointEntity(name, point?.toString(), placeId)

fun CityPointEntity.map() = CityPoint(name ?: "", mapPoint(), placeId ?: "")
