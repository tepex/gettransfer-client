package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.repository.AddressRepository

class AddressInteractor(private val repository: AddressRepository)
{
	fun getAddressByLocation(point: Point) = repository.getAddressByLocation(point)
	fun getCachedAddress() = repository.getCachedAddress()
	fun getAutocompletePredictions(prediction: String) = repository.getAutocompletePredictions(prediction)
	fun releaseAutocompletePredictionsResources() = repository.releaseAutocompletePredictionsResources()
}
