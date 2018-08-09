package com.kg.gettransfer.data.repository

import android.location.Geocoder

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.repository.AddressRepository

import kotlin.collections.List

class AddressRepositoryImpl(private val geocoder: Geocoder): AddressRepository {
	override fun getAddressByLocation(point: Point): String? {
		/*
		return Single.create({emitter ->
			val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
			if(!emitter.isDisposed())
			{
				if(addresses == null || addresses.size == 0)
					emitter.onError(UnknownAddressException("Address is unknown"))
				else emitter.onSuccess(addresses.get(0).getAddressLine(0))
			}
		})
		*/
		return "This is address"
	}
}
