package com.kg.gettransfer.data.repository

import android.location.Address
import android.location.Geocoder

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.repository.AddressRepository

class AddressRepositoryImpl(private val geocoder: Geocoder): AddressRepository {
	override fun getAddressByLocation(point: Point): GTAddress? {
		val list = geocoder.getFromLocation(point.latitude, point.longitude, 1)
		val addr = list?.firstOrNull()?.getAddressLine(0)
		return if(addr != null) GTAddress(addr)
		else null 
	}
}
