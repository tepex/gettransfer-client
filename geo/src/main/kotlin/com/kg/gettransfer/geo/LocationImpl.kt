package com.kg.gettransfer.geo

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import com.kg.gettransfer.data.Location
import com.kg.gettransfer.data.LocationException
import com.kg.gettransfer.data.model.LocationEntity
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationImpl(private val context: Context) : Location {
    private lateinit var geocoder: Geocoder
    private val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    override val isGpsEnabled: Boolean
        get() = (context.getSystemService(Context.LOCATION_SERVICE)
                as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)

    override fun initGeocoder(locale: Locale) {
        //geocoder = Geocoder(context, locale)
        geocoder = Geocoder(context)
    }

    override suspend fun getCurrentLocation(): LocationEntity = suspendCoroutine { cont ->
        locationProviderClient.lastLocation
                .addOnSuccessListener { l: android.location.Location -> cont.resume(LocationEntity(l.latitude, l.longitude)) }
                .addOnFailureListener { throw LocationException(LocationException.NOT_FOUND, "Unknown") }
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
}