package com.kg.gettransfer.domain

class GeoException(val code: Int, val details: String) : RuntimeException(details) {

    companion object {
        const val NOT_FOUND      = 404
        const val GEOCODER_ERROR = -2
    }
}
