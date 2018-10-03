package com.kg.gettransfer.data.repository

import android.content.Context

import android.location.Geocoder
import android.location.Location

import com.google.android.gms.common.data.DataBufferUtils

import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.PlaceDetectionClient

import com.google.android.gms.tasks.Tasks

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.GeoRepository

import java.util.Locale

import kotlin.coroutines.experimental.suspendCoroutine

import timber.log.Timber
import java.lang.StringBuilder

class GeoRepositoryImpl(private val context: Context): GeoRepository {
    private lateinit var geocoder: Geocoder
    private val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val gdClient = Places.getGeoDataClient(context)
    private val pdClient = Places.getPlaceDetectionClient(context)
 
	override fun initGeocoder(locale: Locale) {
	    Timber.d("init geocoder: $locale")
	    geocoder = Geocoder(context, locale)
	}

    override suspend fun getCurrentLocation(): Result<Point> {
        val location: Location? = try {
            suspendCoroutine { cont ->
                locationProviderClient.lastLocation
                    .addOnSuccessListener { location: Location? -> cont.resume(location) }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
        } catch(e: Exception) { return Result(error = e) }
        return if(location != null) Result(Point(location.latitude, location.longitude))
        else Result(error = RuntimeException("Location not found"))
    }

    override fun getAddressByLocation(point: Point): GTAddress {
        val list = geocoder.getFromLocation(point.latitude, point.longitude, 1)
        /*val addr = list?.firstOrNull()?.thoroughfare + " " + list?.firstOrNull()?.subThoroughfare
        if(addr == null) throw RuntimeException("Address not found") */

        val street = list.firstOrNull()?.thoroughfare
        val house = list.firstOrNull()?.subThoroughfare
        val city = list.firstOrNull()?.locality
        val area = list.firstOrNull()?.adminArea
        val country = list.firstOrNull()?.countryName

        val addr = StringBuilder()

        if(street == null && list.firstOrNull()?.getAddressLine(0)!!.isNotEmpty()) {
            addr.append(list.firstOrNull()?.getAddressLine(0))
        }
        else {
            if(!street.isNullOrEmpty())  addr.append(street).append(", ")
            if(!house.isNullOrEmpty())   addr.append(house).append(", ")
            if(!city.isNullOrEmpty())    addr.append(city).append(", ")
            if(!country.isNullOrEmpty()) addr.append(country)
            if(!area.isNullOrEmpty() && area != city) addr.append(area).append(", ")
        }

        val text = getAutocompletePredictions(addr.toString())
        val address = if(text.isNotEmpty()) text.get(0).address else addr.toString()
        return GTAddress(placeTypes = listOf(GTAddress.TYPE_STREET_ADDRESS),
                         name = address,
                         address = address,
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
                         null,
                         null,
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
            GTAddress(it.placeId,
                      it.placeTypes,
                      it.getPrimaryText(null).toString(),
                      it.getFullText(null).toString(),
                      it.getPrimaryText(null).toString(),
                      it.getSecondaryText(null).toString(),
                      null)
        }
    }

    override fun getLatLngByPlaceId(placeId: String): Point {
        val results = gdClient.getPlaceById(placeId)
        Tasks.await(results)
        val place = DataBufferUtils.freezeAndClose(results.getResult()).first()
        return Point(place.latLng.latitude, place.latLng.longitude)
    }
}
