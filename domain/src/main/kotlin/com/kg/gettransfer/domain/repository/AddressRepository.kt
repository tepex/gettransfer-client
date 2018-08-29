package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

interface AddressRepository
{
	/*
	fun getAddressByLocation(point: Point): GTAddress
	fun getCachedAddress(): GTAddress?
	*/
	
	fun getCurrentAddress(): GTAddress
	fun getAutocompletePredictions(prediction: String): List<GTAddress>
	fun getLatLngByPlaceId(placeId: String): Point
}
