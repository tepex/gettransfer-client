package com.kg.gettransfer.data.model

data class LocationEntity(val latitude: Double = 0.0,
                          val longitude: Double = 0.0) {
    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
    }
}