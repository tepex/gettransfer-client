package com.kg.gettransfer.geo

import android.content.Context

import android.location.Geocoder
import android.location.Location

import com.google.android.gms.common.data.DataBufferUtils

import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.google.android.gms.tasks.Tasks

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.GeoRepository
import java.io.IOException

import java.util.Locale

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GeoRepositoryImpl(private val context: Context): GeoRepository {
    private lateinit var geocoder: Geocoder
    private val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val gdClient = Places.getGeoDataClient(context)
    private val pdClient = Places.getPlaceDetectionClient(context)

    override fun initGeocoder(locale: Locale) {
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

    override fun getAddressByLocation(point: Point, pair: Pair<Point, Point>): GTAddress {
        try {
            val list = geocoder.getFromLocation(point.latitude, point.longitude, 1)

            val street = list.firstOrNull()?.thoroughfare
            val house = list.firstOrNull()?.subThoroughfare
            val city = list.firstOrNull()?.locality
            val area = list.firstOrNull()?.adminArea
            val country = list.firstOrNull()?.countryName

            val addr = with(StringBuilder()) {
                if (street == null && !list.isEmpty() && list.firstOrNull()?.getAddressLine(0)!!.isNotEmpty()) {
                    append(list.firstOrNull()?.getAddressLine(0))
                } else {
                    if (!street.isNullOrEmpty()) append(street).append(", ")
                    if (!house.isNullOrEmpty()) append(house).append(", ")
                    if (!city.isNullOrEmpty()) append(city).append(", ")
                    if (!country.isNullOrEmpty()) append(country)
                    if (!area.isNullOrEmpty() && area != city) append(area).append(", ")
                }
                toString()
            }
            val text = getAutocompletePredictions(addr, pair)
            val address = if (text.isNotEmpty()) text.get(0).address else addr
            return GTAddress(CityPoint(address, point, null),
                    listOf(GTAddress.TYPE_STREET_ADDRESS),
                    address,
                    null,
                    null)
        } catch (e: IOException) {
            throw e
        }
    }

    override fun getCurrentAddress(): GTAddress {
        val results = pdClient.getCurrentPlace(null)
        Tasks.await(results)
        val list = DataBufferUtils.freezeAndClose(results.getResult())
        if(list.isEmpty()) throw RuntimeException("Address not found")

        val place = list.first().place
        val cityPoint = CityPoint(place.name.toString(), Point(place.latLng.latitude, place.latLng.longitude), place.id)
        return GTAddress(cityPoint,
                         place.placeTypes,
                         place.address.toString(),
                         null,
                         null)
    }

    /**
     * @TODO: Добавить таймаут
     */
    override fun getAutocompletePredictions(prediction: String, points: Pair<Point, Point>?): List<GTAddress> {
        var bounds: LatLngBounds? = null
        if(points != null){
            val northEastPoint = LatLng(points.first.latitude,points.first.longitude)
            val southWestPoint = LatLng(points.second.latitude,points.second.longitude)
            bounds = LatLngBounds(southWestPoint,northEastPoint)
        }
        val results = gdClient.getAutocompletePredictions(prediction, bounds, null)
        Tasks.await(results)
        val list = DataBufferUtils.freezeAndClose(results.getResult())
        return list.map {
            GTAddress(CityPoint(it.getPrimaryText(null).toString(), null, it.placeId),
                      it.placeTypes,
                      it.getFullText(null).toString(),
                      it.getPrimaryText(null).toString(),
                      it.getSecondaryText(null).toString())
        }
    }

    override fun getLatLngByPlaceId(placeId: String): Point {
        val results = gdClient.getPlaceById(placeId)
        Tasks.await(results)
        val place = DataBufferUtils.freezeAndClose(results.getResult()).first()
        return Point(place.latLng.latitude, place.latLng.longitude)
    }
}
