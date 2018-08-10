package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.Utils
import com.kg.gettransfer.domain.repository.LocationRepository

import kotlinx.coroutines.experimental.*

class LocationInteractor(cc: CoroutineContexts,
	                     private val repository: LocationRepository) {
	val utils: Utils
	val compositeDisposable = Job()
	
	init {
		utils = Utils(cc)
	}
	
	fun checkLocationServicesAvailability() = utils.launchAsync(compositeDisposable) {
		repository.checkPlayServicesAvailable()
	}
	
	fun getCurrentLocation() = utils.launchAsync(compositeDisposable) {
		repository.getCurrentLocation()
	}
	
	fun cancel() { compositeDisposable.cancel() }
}
