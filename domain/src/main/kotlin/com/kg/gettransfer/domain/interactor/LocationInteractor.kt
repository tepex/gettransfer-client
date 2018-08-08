package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.LocationRepository

class LocationInteractor(val repository: LocationRepository) {
	fun checkLocationServicesAvailability() = repository.checkPlayServicesAvailable()
}
