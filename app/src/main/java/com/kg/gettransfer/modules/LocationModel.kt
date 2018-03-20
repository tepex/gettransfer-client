package com.kg.gettransfer.modules


import android.location.Geocoder
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.data.LocationDetailed
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Created by denisvakulenko on 19/03/2018.
 */


class LocationModel(
        private val googleApiClient: GoogleApiClient,
        private val geocoder: Geocoder)
    : AsyncModel(), KoinComponent {

    var onLocationChanged: Runnable? = null

    var location: LocationDetailed? = null
        set(newLocation) {
            if (newLocation?.equalsRaw(field) == true &&
                    newLocation.latLng == null &&
                    newLocation.placeID == null) return

            field = newLocation
            onLocationChanged?.run()

            if (newLocation?.valid == false) validate(newLocation)
        }

    private fun validate(location: LocationDetailed): LocationDetailed {
        if (!googleApiClient.isConnected) {
            throw Exception("Google API client is not connected")
        }

        if (location.placeID != null) {
            busy = true

            val pendingResult = Places.GeoDataApi.getPlaceById(googleApiClient, location.placeID)

            pendingResult.setResultCallback({ places ->
                if (location.placeID == this.location?.placeID &&
                        location.title == this.location?.title) {
                    if (places.status.isSuccess) {
                        this.location = LocationDetailed(
                                location.title,
                                location.subtitle,
                                location.placeID,
                                places[0].latLng)
                        places.release()
                    } else {
                        err(places.status.statusMessage)
                    }
                    busy = false
                }
            }, 3, TimeUnit.SECONDS)
        } else if (location.title.isNotEmpty()) {
            busy = true
            try {
                disposables.add(Single
                        .fromCallable {
                            val places = geocoder.getFromLocationName(location.title, 1) //, -90.0, -180.0, 90.0, 180.0)
                            if (places.isNotEmpty()) {
                                return@fromCallable LocationDetailed(
                                        location.title,
                                        location.subtitle,
                                        location.placeID,
                                        LatLng(places[0].latitude, places[0].longitude))
                            }
                            throw Exception("No such place")
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it.placeID == this.location?.placeID &&
                                    it.title == this.location?.title) {
                                this.location = it
                                busy = false
                            }
                        }, {
                            if (location.placeID == this.location?.placeID &&
                                    location.title == this.location?.title) {
                                err(it)
                                busy = false
                            }
                        }))
            } catch (e: IOException) {
                e.printStackTrace()
                busy = false
            }
        }

        return location
    }
}