package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.Point

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CityPointEntity(
    @SerialName(NAME) val name: String?,
    @SerialName(POINT) val point: String?,
    @SerialName(PLACE_ID) val placeId: String?
) {

    fun mapPoint(): Point? {
        if (point == null) return null
        val latLng = POINT_REGEX.find(point)!!.groupValues
        return Point(latLng.get(1).toDouble(), latLng.get(2).toDouble())
    }
    
    companion object {
        const val NAME     = "name"
        const val POINT    = "point"
        const val PLACE_ID = "place_id"
        
        private val POINT_REGEX = "\\(([\\d\\.\\-]+)\\,([\\d\\.\\-]+)\\)".toRegex()
    }
}

fun CityPoint.map() = CityPointEntity(name, point?.toString(), placeId)
fun CityPointEntity.map() = CityPoint(name ?: "", mapPoint(), placeId ?: "")
