package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.GeoRemote
import com.kg.gettransfer.data.Location
import com.kg.gettransfer.data.model.LocationEntity
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.Locale

open class GeoDataStore : KoinComponent {
    private val remote: GeoRemote by inject()
    private val location: Location by inject()

    fun isGpsEnabled() = location.isGpsEnabled
    fun initGeocoder(locale: Locale) = location.initGeocoder(locale)
    fun initGoogleApiClient() = location.initGoogleApiClient()
    fun disconnectGoogleApiClient() = location.disconnectGoogleApiClient()

    suspend fun getCurrentLocation() = location.getCurrentLocation()
    suspend fun getMyLocationByIp(ipAddress: String) = remote.getMyLocationByIp(ipAddress)
    fun getAddressByLocation(point: LocationEntity) = location.getAddressByLocation(point)

    suspend fun getAutocompletePredictions(query: String, lang: String) = remote.getAutocompletePredictions(query, lang)
    suspend fun getPlaceDetails(placeId: String, lang: String) = remote.getPlaceDetails(placeId, lang)
}
