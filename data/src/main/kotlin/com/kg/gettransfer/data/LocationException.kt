package com.kg.gettransfer.data

import com.kg.gettransfer.domain.GeoException

class LocationException(
    val code: Int,
    val details: String
) : RuntimeException(details) {

    fun map() = GeoException(code, details)

    companion object {
        @JvmField val NOT_FOUND      = GeoException.NOT_FOUND
        @JvmField val GEOCODER_ERROR = GeoException.GEOCODER_ERROR
    }
}
