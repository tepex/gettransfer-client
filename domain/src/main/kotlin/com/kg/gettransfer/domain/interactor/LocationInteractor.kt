package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.repository.LocationRepository

import kotlinx.coroutines.experimental.*

class LocationInteractor(private val repository: LocationRepository) {
	fun checkLocationServicesAvailability() = repository.checkPlayServicesAvailable()
	suspend fun getCurrentLocation(utils: AsyncUtils): Point {
		val result = utils.asyncAwait { repository.getCurrentLocation() }
		if(result.error != null) throw result.error
		return result.result as Point 
	}
}
