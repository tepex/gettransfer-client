package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Point
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetailsResultEntity(
    @SerialName(RESULT) val result: PlaceDetailsEntity?
) {
    companion object {
        const val RESULT = "result"
    }
}

@Serializable
data class PlaceDetailsEntity(
    @SerialName(ADDRESS_COMPONENTS)   val addressComponents: List<AddressComponentEntity>,
    @SerialName(GT_FORMATTED_ADDRESS) val formattedAddress: String,
    @SerialName(LOCATION)             val location: PlaceLocationEntity,
    @SerialName(NAME)                 val name: String,
    @SerialName(TYPES)                val types: List<String>
) {
    companion object {
        const val ADDRESS_COMPONENTS   = "address_components"
        const val GT_FORMATTED_ADDRESS = "gt_formatted_address"
        const val LOCATION             = "location"
        const val NAME                 = "name"
        const val TYPES                = "types"
    }
}

@Serializable
data class AddressComponentEntity(
    @SerialName(LONG_NAME)  val longName: String,
    @SerialName(SHORT_NAME) val shortName: String,
    @SerialName(TYPES)      val types: List<String>
) {
    companion object {
        const val LONG_NAME  = "long_name"
        const val SHORT_NAME = "short_name"
        const val TYPES      = "types"
    }
}

@Serializable
data class PlaceLocationEntity(
    @SerialName(LAT) val lat: Double,
    @SerialName(LNG) val lng: Double
) {
    companion object {
        const val LAT = "lat"
        const val LNG = "lng"
    }
}

fun PlaceLocationEntity.map() = Point(lat, lng)
