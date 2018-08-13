package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.LocationRepository

import kotlinx.coroutines.experimental.*

class LocationInteractor(private val repository: LocationRepository) {
	fun checkLocationServicesAvailability() = repository.checkPlayServicesAvailable()
	suspend fun getCurrentLocation() = repository.getCurrentLocation()
}
