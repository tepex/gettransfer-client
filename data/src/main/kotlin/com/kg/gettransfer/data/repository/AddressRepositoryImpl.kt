package com.kg.gettransfer.data.repository

import android.location.Geocoder

import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.*

import com.google.android.gms.tasks.Tasks

import com.kg.gettransfer.data.AddressCache

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.repository.AddressRepository

import kotlin.coroutines.experimental.suspendCoroutine

import timber.log.Timber

class AddressRepositoryImpl(private val geocoder: Geocoder,
                            private val gdClient: GeoDataClient,
                            private val pdClient: PlaceDetectionClient) : AddressRepository {
    private val addressCache = AddressCache()

    override fun getAddressByLocation(point: Point): GTAddress {
        var address = addressCache.getAddress(point)
        if(address == null) {
            val list = geocoder.getFromLocation(point.latitude, point.longitude, 1)
            val addr = list?.firstOrNull()?.getAddressLine(0)
            if(addr != null) {
                address = GTAddress(placeTypes = listOf(GTAddress.TYPE_STREET_ADDRESS), name = addr, primary = null, secondary = null, point = point)
                addressCache.putAddress(address)
            }
            else throw RuntimeException("Address not found")
        }
        return address
    }

    override fun getCachedAddress() = addressCache.lastAddress

    override fun getCurrentAddress(): GTAddress {
        val results = pdClient.getCurrentPlace(null)
        Tasks.await(results)
        val list = DataBufferUtils.freezeAndClose(results.getResult())
        if (list.isEmpty()) throw RuntimeException("Address not found")

        val place = list.get(0).place
        val address = GTAddress(place.id, place.placeTypes,
                place.name.toString(), // place.address.toString()
                null, null,
                Point(place.latLng.latitude, place.latLng.longitude))
        addressCache.putAddress(address)
        return address
    }

    /**
     * @TODO: Добавить таймаут
     */
    override fun getAutocompletePredictions(prediction: String): List<GTAddress> {
        val results = gdClient.getAutocompletePredictions(prediction, null, null)
        Tasks.await(results)
        val list = DataBufferUtils.freezeAndClose(results.getResult())
        val ret = list.map {
            GTAddress(it.placeId, it.placeTypes,
                    it.getFullText(null).toString(),
                    it.getPrimaryText(null).toString(),
                    it.getSecondaryText(null).toString(),
                    null)
        }
        ret.forEach { Timber.d(it.toString()) }
        return ret
    }

    override fun getLatLngByPlaceId(placeId: String): Point {
        val results = gdClient.getPlaceById(placeId)
        Tasks.await(results)
        val list = DataBufferUtils.freezeAndClose(results.getResult())
        val place = list.get(0)
        return Point(place.latLng.latitude, place.latLng.longitude)
    }
}
