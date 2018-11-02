package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result

import java.util.Locale

interface GeoRepository {
    fun initGeocoder(locale: Locale)
//    fun checkPlayServicesAvailable(): Boolean
    suspend fun getCurrentLocation(): Result<Point>
    fun getAddressByLocation(point: Point, pair: Pair<Point, Point>): GTAddress
    fun getCurrentAddress(): GTAddress

    fun getAutocompletePredictions(prediction: String, points: Pair<Point, Point>?): List<GTAddress>

    fun getLatLngByPlaceId(placeId: String): Point
}
