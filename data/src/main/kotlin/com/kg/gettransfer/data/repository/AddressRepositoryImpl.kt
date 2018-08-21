package com.kg.gettransfer.data.repository

import android.location.Address
import android.location.Geocoder

import com.google.android.gms.common.data.DataBufferUtils

import com.google.android.gms.location.places.AutocompletePredictionBufferResponse
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceDetectionClient

import com.google.android.gms.tasks.Tasks

import com.kg.gettransfer.data.AddressCache

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.repository.AddressRepository

import kotlin.coroutines.experimental.suspendCoroutine

import timber.log.Timber

class AddressRepositoryImpl(private val geocoder: Geocoder,
	                        private val gdClient: GeoDataClient,
	                        private val pdClient: PlaceDetectionClient): AddressRepository {
	private val addressCache = AddressCache()
	
	override fun getAddressByLocation(point: Point): GTAddress {
		var address = addressCache.getAddress(point)
		if(address == null) {
			val list = geocoder.getFromLocation(point.latitude, point.longitude, 1)
			val addr = list?.firstOrNull()?.getAddressLine(0)
			if(addr != null) {
				address = GTAddress(address = addr, point = point)
				addressCache.putAddress(address)
			}
			else throw RuntimeException("Address not found")
		}
		return address
	}
	
	override fun getCachedAddress() = addressCache.getLastAddress()
	
	/*
	override fun getCurrentAddress(): GTAddress? {
		val results = pdClient.getCurrentPlace(null)
		Tasks.await(results)
		val list = DataBufferUtils.freezeAndClose(results.getResult())
		return if(!list.isEmpty()) {
			val place = list.get(0).place
			Timber.d("Current place name: %s", place.name)
			GTAddress(place.id, place.placeTypes, place.address.toString(), 
				Point(place.latLng.latitude, place.latLng.longitude))
		}
		else null
	}
	*/
	
	/**
	 * @TODO: Добавить таймаут
	 */
	override fun getAutocompletePredictions(prediction: String): List<GTAddress> {
		val results = gdClient.getAutocompletePredictions(prediction, null, null)
		Tasks.await(results)
		val list = DataBufferUtils.freezeAndClose(results.getResult())
		val ret = list.map { GTAddress(it.placeId, it.placeTypes, it.getPrimaryText(null).toString()) }
		ret.forEach { Timber.d(it.toString()) }
		return ret
	}
}
