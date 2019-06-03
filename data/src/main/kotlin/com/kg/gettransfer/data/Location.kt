package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.LocationEntity
import java.util.Locale

interface Location {
    val isGpsEnabled: Boolean
    fun initGeocoder(locale: Locale)
    suspend fun getCurrentLocation(): LocationEntity
    fun getAddressByLocation(point: LocationEntity): String
}