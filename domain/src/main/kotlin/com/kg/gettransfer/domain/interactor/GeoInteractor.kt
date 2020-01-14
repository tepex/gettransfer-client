package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.SessionRepository

class GeoInteractor(
    private val sessionRepository: SessionRepository,
    private val geoRepository: GeoRepository
) {

    val isGpsEnabled
        get() = geoRepository.isGpsEnabled

    fun initGeocoder() = geoRepository.initGeocoder(sessionRepository.account.locale)

    fun initGoogleApiClient() = geoRepository.initGoogleApiClient()

    fun disconnectGoogleApiClient() = geoRepository.disconnectGoogleApiClient()

    suspend fun getCurrentLocation() = geoRepository.getCurrentLocation()
    suspend fun getMyLocationByIp(ipAddress: String) = geoRepository.getMyLocationByIp(ipAddress)
    suspend fun getAddressByLocation(point: Point) =
        geoRepository.getAddressByLocation(point, sessionRepository.account.locale.language)
}
