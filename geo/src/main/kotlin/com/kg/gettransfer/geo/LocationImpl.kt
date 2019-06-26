package com.kg.gettransfer.geo

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager

import android.os.Bundle

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
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
        @Suppress("UnsafeCast")
        get() = (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
            .isProviderEnabled(LocationManager.GPS_PROVIDER)

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
        locationProviderClient.lastLocation
            // In some rare situations Location can be null.
            // https://developer.android.com/training/location/retrieve-current#last-known
            .addOnSuccessListener { l: android.location.Location? ->
                l?.let { cont.resume(LocationEntity(it.latitude, it.longitude)) }
            }
            .addOnFailureListener {
                cont.resumeWithException(LocationException(LocationException.NOT_FOUND, "Unknown"))
            }
    }

    @Suppress("ComplexMethod")
    override fun getAddressByLocation(point: LocationEntity): String {
        val list = try {
            geocoder.getFromLocation(point.latitude, point.longitude, 1)
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            throw LocationException(LocationException.GEOCODER_ERROR, e.message ?: "Unknown")
        }
        if (list.isEmpty()) return ""

        /* TODO: refactoring needed */
        val addr = list.first()
        val street = addr.thoroughfare
        val line = addr.getAddressLine(0) ?: ""
        val house = addr.subThoroughfare
        val city = addr.locality
        val area = addr.adminArea
        val country = addr.countryName

        return buildString {
            when {
                street == null && line.isNotEmpty()                 -> append(line)
                !street.isNullOrEmpty() && street != "Unnamed Road" -> append(street).append(", ")
                !house.isNullOrEmpty()                              -> append(house).append(", ")
                !city.isNullOrEmpty()                               -> append(city).append(", ")
                !country.isNullOrEmpty()                            -> append(country).append(", ")
                !area.isNullOrEmpty() && area != city               -> append(area).append(", ")
                this.lastIndexOf(", ") == this.length - 2           -> delete(this.length - 2, this.length)
            }
        }
    }

    override fun onConnected(p0: Bundle?) {}

    override fun onConnectionSuspended(p0: Int) {}

    override fun onConnectionFailed(p0: ConnectionResult) {}
}
