package com.kg.gettransfer.data.repository

import android.location.Geocoder

import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.tasks.Tasks

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.repository.AddressRepository

class AddressRepositoryImpl(private val geocoder: Geocoder,
                            private val gdClient: GeoDataClient,
                            private val pdClient: PlaceDetectionClient): AddressRepository {
    override fun getAddressByLocation(point: Point): GTAddress {
        val list = geocoder.getFromLocation(point.latitude, point.longitude, 1)
        val addr = list?.firstOrNull()?.getAddressLine(0)
        if(addr == null) throw RuntimeException("Address not found") 
        return GTAddress(placeTypes = listOf(GTAddress.TYPE_STREET_ADDRESS),
                         name = addr,
                         address = addr,
                         primary = null,
                         secondary = null,
                         point = point)
    }

    override fun getCurrentAddress(): GTAddress {
        val results = pdClient.getCurrentPlace(null)
        Tasks.await(results)
        val list = DataBufferUtils.freezeAndClose(results.getResult())
        if(list.isEmpty()) throw RuntimeException("Address not found")

        val place = list.first().place
        return GTAddress(place.id,
                                place.placeTypes,
                                place.name.toString(),
                                place.address.toString(),
                                null, null,
                                Point(place.latLng.latitude, place.latLng.longitude))
    }

    /**
     * @TODO: Добавить таймаут
     */
    override fun getAutocompletePredictions(prediction: String): List<GTAddress> {
        val results = gdClient.getAutocompletePredictions(prediction, null, null)
        Tasks.await(results)
        val list = DataBufferUtils.freezeAndClose(results.getResult())
        return list.map {
            GTAddress(it.placeId, it.placeTypes,
                      it.getFullText(null).toString(),
                      it.getPrimaryText(null).toString(),
                      it.getSecondaryText(null).toString(),
                      null)
        }
    }

    override fun getLatLngByPlaceId(placeId: String): Point {
        val results = gdClient.getPlaceById(placeId)
        Tasks.await(results)
        val list = DataBufferUtils.freezeAndClose(results.getResult())
        val place = list.first()
        return Point(place.latLng.latitude, place.latLng.longitude)
    }
}
