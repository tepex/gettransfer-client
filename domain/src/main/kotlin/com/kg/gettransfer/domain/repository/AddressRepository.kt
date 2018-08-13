package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
//import com.kg.gettransfer.domain.model.Result

interface AddressRepository
{
	fun getAddressByLocation(point: Point): GTAddress?
	fun getCachedAddress(): GTAddress?
	/*
	fun getAutocompletePredictions(prediction: String): Result<List<String>>
	fun releaseAutocompletePredictionsResources()
	*/
}
