package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.GeoRemote
import com.kg.gettransfer.data.Location
import com.kg.gettransfer.data.model.LocationEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.Locale

open class GeoDataStore : KoinComponent {
    private val remote: GeoRemote by inject()
    private val location: Location by inject()

    fun isGpsEnabled() = location.isGpsEnabled
    fun initGeocoder(locale: Locale) = location.initGeocoder(locale)

    suspend fun getCurrentLocation() = location.getCurrentLocation()
    suspend fun getMyLocationByIp() = remote.getMyLocationByIp()
    suspend fun getAddressByLocation(point: LocationEntity) = location.getAddressByLocation(point)

    suspend fun getAutocompletePredictions(query: String, lang: String) = remote.getAutocompletePredictions(query, lang)
    suspend fun getPlaceDetails(placeId: String, lang: String) = remote.getPlaceDetails(placeId, lang)
}