package com.kg.gettransfer.data.model

data class LocationEntity(val latitude: Double?,
                          val longitude: Double?) {
    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
    }
}