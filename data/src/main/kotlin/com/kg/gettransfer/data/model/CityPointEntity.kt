package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName

data class CityPointEntity(
    @SerialName(NAME) val name: String,
    @SerialName(POINT) val point: String?,
    @SerialName(PLACE_ID) val placeId: String?
) {

    companion object {
        const val NAME     = "name"
        const val POINT    = "point"
        const val PLACE_ID = "place_id"
    }
}
