package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.LocationRepository

class LocationInteractor(val repository: LocationRepository) {
	suspend fun checkLocationServicesAvailability() = repository.checkPlayServicesAvailable()
	suspend fun getCurrentLocation() = repository.getCurrentLocation()
}
