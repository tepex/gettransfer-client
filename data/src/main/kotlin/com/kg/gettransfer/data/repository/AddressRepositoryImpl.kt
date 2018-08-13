package com.kg.gettransfer.data.repository

import android.location.Address
import android.location.Geocoder

import com.kg.gettransfer.data.AddressCache

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.repository.AddressRepository

class AddressRepositoryImpl(private val geocoder: Geocoder): AddressRepository {
	private val addressCache = AddressCache()
	
	override fun getAddressByLocation(point: Point): GTAddress? {
		var address = addressCache.getAddress(point)
		if(address == null) {
			val list = geocoder.getFromLocation(point.latitude, point.longitude, 1)
			val addr = list?.firstOrNull()?.getAddressLine(0)
			if(addr != null) {
				address = GTAddress(addr)
				addressCache.putAddress(point, address)
			}
		}
		return address
	}
}
