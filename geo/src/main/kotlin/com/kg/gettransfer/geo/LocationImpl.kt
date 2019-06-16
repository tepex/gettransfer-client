package com.kg.gettransfer.geo

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager

import android.os.Bundle
import android.os.Looper

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.kg.gettransfer.data.Location
import com.kg.gettransfer.data.LocationException
import com.kg.gettransfer.data.model.LocationEntity

import java.util.Locale

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationImpl(private val context: Context) : 
    Location, 
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private lateinit var geocoder: Geocoder
    private val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var googleApiClient: GoogleApiClient? = null

    override val isGpsEnabled: Boolean
        get() = (context.getSystemService(Context.LOCATION_SERVICE)
                as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)

    override fun initGeocoder(locale: Locale) {
        geocoder = Geocoder(context, locale)
    }

    override fun initGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build()
        }
        googleApiClient?.connect()
    }

    override fun disconnectGoogleApiClient() {
        googleApiClient?.let { googleApiClient?.disconnect() }
    }

    override suspend fun getCurrentLocation(): LocationEntity = suspendCoroutine { cont ->
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) return
            }
        }
        locationProviderClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper())
        locationProviderClient.lastLocation
                // In some rare situations Location can be null. https://developer.android.com/training/location/retrieve-current#last-known
                .addOnSuccessListener { l: android.location.Location? -> l?.let { cont.resume(LocationEntity(it.latitude, it.longitude)) } }
                .addOnFailureListener { cont.resumeWithException(LocationException(LocationException.NOT_FOUND, "Unknown")) }
        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest() = LocationRequest.create()?.apply {
        interval = LOCATION_UPDATE_INTERVAL
        fastestInterval = LOCATION_UPDATE_FASTEST_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun getAddressByLocation(point: LocationEntity): String {
        val list = try {
            geocoder.getFromLocation(point.latitude, point.longitude, 1)
        } catch (e: Exception) {
            throw LocationException(LocationException.GEOCODER_ERROR, e.message ?: "Unknown")
        }

        val street = list.firstOrNull()?.thoroughfare
        val house = list.firstOrNull()?.subThoroughfare
        val city = list.firstOrNull()?.locality
        val area = list.firstOrNull()?.adminArea
        val country = list.firstOrNull()?.countryName

        return buildString {
            if (street == null && !list.isEmpty() && list.firstOrNull()?.getAddressLine(0)!!.isNotEmpty())
                append(list.firstOrNull()?.getAddressLine(0))
            else {
                if (!street.isNullOrEmpty()) append(street).append(", ")
                if (!house.isNullOrEmpty()) append(house).append(", ")
                if (!city.isNullOrEmpty()) append(city).append(", ")
                if (!country.isNullOrEmpty()) append(country)
                if (!area.isNullOrEmpty() && area != city) append(area).append(", ")
            }
        }
    }

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 10000L
        private const val LOCATION_UPDATE_FASTEST_INTERVAL = 5000L
    }
}
