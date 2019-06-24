package com.kg.gettransfer.data

import com.kg.gettransfer.domain.GeoException

class LocationException(
    val code: Int,
    val details: String
) : RuntimeException(details) {

    fun map() = GeoException(code, details)

    companion object {
        const val NOT_FOUND      = GeoException.NOT_FOUND
        const val GEOCODER_ERROR = GeoException.GEOCODER_ERROR
    }
}
