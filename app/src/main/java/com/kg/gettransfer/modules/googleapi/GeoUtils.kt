package com.kg.gettransfer.modules.googleapi


import android.location.Geocoder
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.maps.PlacesApi
import com.jakewharton.rxrelay2.PublishRelay
import com.kg.gettransfer.data.LocationDetailed
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Created by denisvakulenko on 12/02/2018.
 */


class GeoUtils(
        private val googleApiClient: GoogleApiClient,
        private val geocoder: Geocoder)
    : KoinComponent {

    private val TAG = "GeoUtils"

    private var response: PublishRelay<LocationDetailed> = PublishRelay.create()


    fun subscribe(c: (LocationDetailed) -> Unit): Disposable {
        return response
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(c, {})
    }


    // Blocks thread
    fun validate(location: LocationDetailed): LocationDetailed {
        if (!googleApiClient.isConnected) {
            Log.e(TAG, "Google API client is not connected")
            throw Exception("Google API client is not connected")
        }

        Log.i(TAG, "Validate GEO: " + location.title)

        if (location.placeID != null) {
//            PlacesApi.placeDetails()

            val pendingResult = Places.GeoDataApi.getPlaceById(googleApiClient, location.placeID)

            pendingResult.setResultCallback({ places ->
                if (places.status.isSuccess) {
                    Log.i(TAG, "Validated: " + places[0].toString())
                    response.accept(
                            LocationDetailed(
                                    location.title,
                                    location.subtitle,
                                    location.placeID,
                                    places[0].latLng))
                    places.release()
                }
            }, 3, TimeUnit.SECONDS)
        } else if (location.title.isNotEmpty()) {
            try {
                Single.fromCallable {
                    val places = geocoder.getFromLocationName(location.title, 1) //, -90.0, -180.0, 90.0, 180.0)
                    if (places.isNotEmpty()) {
                        Log.i(TAG, "Validated: " + places[0].featureName)
                        return@fromCallable LocationDetailed(
                                location.title,
                                location.subtitle,
                                location.placeID,
                                LatLng(places[0].latitude, places[0].longitude))
                    }
                    throw Exception()
                }.subscribeOn(Schedulers.newThread()).subscribe({ response.accept(it) }, {})
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return location
    }
}