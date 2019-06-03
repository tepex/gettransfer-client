package com.kg.gettransfer.domain

import java.lang.RuntimeException

class GeoException(val code: Int, val details: String) : RuntimeException(details) {
    companion object {
        @JvmField val NOT_FOUND      = 404
        @JvmField val GEOCODER_ERROR = -2
    }
}