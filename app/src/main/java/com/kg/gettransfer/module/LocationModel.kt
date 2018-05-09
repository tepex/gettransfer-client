package com.kg.gettransfer.module


import android.location.Geocoder
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.data.LocationDetailed
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import java.util.concurrent.TimeUnit


/**
 * Created by denisvakulenko on 19/03/2018.
 */


class LocationModel(
        private val googleApiClient: GoogleApiClient,
        private val geocoder: Geocoder)
    : AsyncModel(), KoinComponent {

    var onLocationChanged: Runnable? = null

    var location: LocationDetailed = LocationDetailed("")
        set(newLocation) {
            if (field == newLocation) {
                field = newLocation
                return
            }

            field = newLocation

            onLocationChanged?.run()

            if (!newLocation.isValid()) validate()
            else busy = false
        }

    fun validate(): LocationDetailed {
        val location = location

        when {
            location.placeID != null -> {
                if (!googleApiClient.isConnected) throw Exception("Google API client is not connected")

                busy = true

                Places.GeoDataApi
                        .getPlaceById(googleApiClient, location.placeID)
                        .setResultCallback({ places ->
                            if (location == this.location) {
                                if (places.status.isSuccess) {
                                    this.location = LocationDetailed(
                                            location.title,
                                            location.subtitle,
                                            location.placeID,
                                            places[0].latLng,
                                            true)
                                } else {
                                    location.validationSuccess = false
                                    this.location = location
                                    err(places.status.statusMessage)
                                }
                                places.release()
                                busy = false
                            }
                        }, 3, TimeUnit.SECONDS)
            }

            location.title.isNotEmpty() ->
                disposables.add(Single
                        .fromCallable {
                            val places = geocoder.getFromLocationName(location.title, 1) //, -90.0, -180.0, 90.0, 180.0)
                            if (places.isNotEmpty()) {
                                return@fromCallable LocationDetailed(
                                        location.title,
                                        location.subtitle,
                                        location.placeID,
                                        LatLng(places[0].latitude, places[0].longitude),
                                        true)
                            }
                            throw Exception("Unknown address")
                        }
                        .subscribeOn(Schedulers.io()).doOnSubscribe { busy = true }
                        .observeOn(AndroidSchedulers.mainThread()).doFinally { busy = false }
                        .subscribe({
                            if (location == this.location) {
                                this.location = it
                            }
                        }, {
                            if (location == this.location) {
                                location.validationSuccess = false
                                this.location = location
                                err(it)
                            }
                        }))

            location.latLng != null ->
                disposables.add(Single
                        .fromCallable {
                            val places = geocoder.getFromLocation(location.latLng.latitude, location.latLng.longitude, 1) //, -90.0, -180.0, 90.0, 180.0)
                            if (places.isNotEmpty()) {
                                return@fromCallable LocationDetailed(
                                        places[0].getAddressLine(0),
                                        location.subtitle,
                                        location.placeID,
                                        LatLng(places[0].latitude, places[0].longitude),
                                        true,
                                        location.myLocation)
                            }
                            throw Exception("Unknown address")
                        }
                        .subscribeOn(Schedulers.io()).doOnSubscribe { busy = true }
                        .observeOn(AndroidSchedulers.mainThread()).doFinally { busy = false }
                        .subscribe({
                            if (location == this.location) {
                                this.location = it
                            }
                        }, {
                            if (location == this.location) {
                                location.validationSuccess = false
                                this.location = location
                                err(it)
                            }
                        }))
        }

        return location
    }
}