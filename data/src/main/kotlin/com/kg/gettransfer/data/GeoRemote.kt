package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AutocompletePredictionsEntity
import com.kg.gettransfer.data.model.LocationEntity
import com.kg.gettransfer.data.model.PlaceDetailsResultEntity

import org.koin.core.KoinComponent

interface GeoRemote : KoinComponent {
    suspend fun getMyLocationByIp(): LocationEntity

    suspend fun getAutocompletePredictions(query: String, lang: String): AutocompletePredictionsEntity
    suspend fun getPlaceDetails(placeId: String, lang: String): PlaceDetailsResultEntity
}
