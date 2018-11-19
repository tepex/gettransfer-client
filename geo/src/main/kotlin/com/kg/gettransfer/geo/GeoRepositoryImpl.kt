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

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.GeoRepository

import java.util.Locale

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

import timber.log.Timber

class GeoRepositoryImpl(private val context: Context): GeoRepository {
    private lateinit var geocoder: Geocoder
    private val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val gdClient = Places.getGeoDataClient(context)
    private val pdClient = Places.getPlaceDetectionClient(context)

    override fun initGeocoder(locale: Locale) {
        geocoder = Geocoder(context, locale)
    }
    
    override suspend fun getCurrentLocation(): Result<Point> = suspendCoroutine { cont ->
        locationProviderClient.lastLocation
            .addOnSuccessListener { l: Location -> cont.resume(Result<Point>(model = Point(l.latitude, l.longitude))) }
            .addOnFailureListener { cont.resume(Result(Point(), ApiException(ApiException.NOT_FOUND, it.message ?: "Unknown"))) }
    }

    override fun getAddressByLocation(point: Point, pair: Pair<Point, Point>): Result<GTAddress> {
        val list = try { geocoder.getFromLocation(point.latitude, point.longitude, 1) }
        catch(e: Exception) { return Result(GTAddress.EMPTY, ApiException(ApiException.GEOCODER_ERROR, e.message ?: "Unknown")) }

        val street  = list.firstOrNull()?.thoroughfare
        val house   = list.firstOrNull()?.subThoroughfare
        val city    = list.firstOrNull()?.locality
        val area    = list.firstOrNull()?.adminArea
        val country = list.firstOrNull()?.countryName

        val addr = buildString {
            if(street == null && !list.isEmpty() && list.firstOrNull()?.getAddressLine(0)!!.isNotEmpty()) {
                append(list.firstOrNull()?.getAddressLine(0))
            }
            else {
                if(!street.isNullOrEmpty())  append(street).append(", ")
                if(!house.isNullOrEmpty())   append(house).append(", ")
                if(!city.isNullOrEmpty())    append(city).append(", ")
                if(!country.isNullOrEmpty()) append(country)
                if(!area.isNullOrEmpty() && area != city) append(area).append(", ")
            }
        }
        
        val result = getAutocompletePredictions(addr, pair)
        if(result.error != null) return Result(GTAddress.EMPTY, result.error)
            
        val address = result.model.firstOrNull()?.address ?: addr
        return Result(GTAddress(CityPoint(address, point, null),
                                listOf(GTAddress.TYPE_STREET_ADDRESS),
                                address,
                                null,
                                null))
    }

    override fun getCurrentAddress(): Result<GTAddress> {
        val results = pdClient.getCurrentPlace(null)
        return try {
            Tasks.await(results)
            val list = DataBufferUtils.freezeAndClose(results.getResult())
            if(list.isEmpty()) Result(GTAddress.EMPTY, ApiException(ApiException.NOT_FOUND, "Address not found"))
            else {
                val place = list.first().place
                val cityPoint = CityPoint(place.name.toString(), Point(place.latLng.latitude, place.latLng.longitude), place.id)
                Result(GTAddress(cityPoint, place.placeTypes, place.address.toString(), null, null))
            }
        } catch(e: Exception) { Result(GTAddress.EMPTY, ApiException(ApiException.NETWORK_ERROR, e.message ?: "Unknown")) }
    }

    override fun getAutocompletePredictions(prediction: String, points: Pair<Point, Point>?): Result<List<GTAddress>> {
        var bounds: LatLngBounds? = null
        if(points != null) {
            val northEastPoint = LatLng(points.first.latitude,points.first.longitude)
            val southWestPoint = LatLng(points.second.latitude,points.second.longitude)
            bounds = LatLngBounds(southWestPoint,northEastPoint)
        }
        val results = gdClient.getAutocompletePredictions(prediction, bounds, null)
        return try {
            Tasks.await(results)
            val list = DataBufferUtils.freezeAndClose(results.getResult())
            Result(list.map { GTAddress(CityPoint(it.getPrimaryText(null).toString(), null, it.placeId),
                                        it.placeTypes,
                                        it.getFullText(null).toString(),
                                        it.getPrimaryText(null).toString(),
                                        it.getSecondaryText(null).toString())
            })
        } catch(e: Exception) { Result(emptyList<GTAddress>(), ApiException(ApiException.NETWORK_ERROR, e.message ?: "Unknown")) } 
    }

    override fun getLatLngByPlaceId(placeId: String): Result<Point> {
        val results = gdClient.getPlaceById(placeId)
        return try {
            Tasks.await(results)
            val place = DataBufferUtils.freezeAndClose(results.getResult()).first()
            Result(Point(place.latLng.latitude, place.latLng.longitude))
        } catch(e: Exception) { Result(Point(), ApiException(ApiException.NETWORK_ERROR, e.message ?: "Unknown")) }
    }
}
