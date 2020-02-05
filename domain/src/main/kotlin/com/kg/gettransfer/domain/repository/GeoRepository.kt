package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.core.domain.GTAddress
import com.kg.gettransfer.core.domain.Point

import com.kg.gettransfer.domain.model.Result

import java.util.Locale

interface GeoRepository {
    val isGpsEnabled: Boolean
    fun initGeocoder(locale: Locale)
    fun initGoogleApiClient()
    fun disconnectGoogleApiClient()

    suspend fun getCurrentLocation(): Result<Point>
    suspend fun getMyLocationByIp(): Result<Point>
    suspend fun getAddressByLocation(point: Point, lang: String): Result<GTAddress>

    suspend fun getAutocompletePredictions(query: String, lang: String): Result<List<GTAddress>>
    suspend fun getPlaceDetails(placeId: String, lang: String): Result<GTAddress>
}
