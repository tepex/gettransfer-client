package com.kg.gettransfer.data.repository

import android.location.Address
import android.location.Geocoder

import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.tasks.Tasks

import com.kg.gettransfer.data.AddressCache

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.repository.AddressRepository

import kotlin.coroutines.experimental.suspendCoroutine

import timber.log.Timber

class AddressRepositoryImpl(private val geocoder: Geocoder, private val gdClient: GeoDataClient): 
	AddressRepository {
	
	private val addressCache = AddressCache()
	
	override fun getAddressByLocation(point: Point): GTAddress? {
		var address = addressCache.getAddress(point)
		if(address == null) {
			val list = try {
				geocoder.getFromLocation(point.latitude, point.longitude, 1)
			} catch(e: Exception) {
				Timber.e(e)
				return null
			}
			val addr = list?.firstOrNull()?.getAddressLine(0)
			if(addr != null) {
				address = GTAddress(address = addr)
				addressCache.putAddress(point, address)
			}
		}
		return address
	}
	
	override fun getCachedAddress() = addressCache.getLastAddress()
	
	/**
	 * @TODO: Добавить таймаут
	 */
	override fun getAutocompletePredictions(prediction: String): List<GTAddress> {
		val results = gdClient.getAutocompletePredictions(prediction, null, null)
		Tasks.await(results)
		val list = DataBufferUtils.freezeAndClose(results.getResult())
		
		//Thread.sleep(3000)
		//return list.map { GTAddress(it.getPrimaryText(null).toString()) }
		val ret = list.map { GTAddress(it.placeId, it.placeTypes, it.getFullText(null).toString()) }
		ret.forEach { Timber.d(it.toString()) }
		return ret
	}
}
