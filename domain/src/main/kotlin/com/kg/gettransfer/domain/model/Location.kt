package com.kg.gettransfer.domain.model

data class Location(val latitude: Double = 0.0,
                    val longitude: Double = 0.0) {
    companion object {
        val EMPTY_LOCATION = Location(0.0, 0.0)
    }
}