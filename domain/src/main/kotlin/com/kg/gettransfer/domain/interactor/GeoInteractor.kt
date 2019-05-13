package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.GeoRemoteRepository
import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.SystemRepository

class GeoInteractor(
        private val systemRepository: SystemRepository,
        private val geoRepository: GeoRepository,
        private val geoRemoteRepository: GeoRemoteRepository) {

    val isGpsEnabled
        get() = geoRepository.isGpsEnabled

    fun initGeocoder() = geoRepository.initGeocoder(systemRepository.account.locale)

    suspend fun getMyLocation() = geoRemoteRepository.getMyLocationByIp()
}