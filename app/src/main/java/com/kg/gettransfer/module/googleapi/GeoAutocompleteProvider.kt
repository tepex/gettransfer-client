package com.kg.gettransfer.module.googleapi


import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePredictionBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLngBounds
import com.kg.gettransfer.data.LocationDetailed
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by denisvakulenko on 12/02/2018.
 */


class GeoAutocompleteProvider : KoinComponent {
    private val TAG = "GeoAutocompleteProvider"

    private val googleApiClient: GoogleApiClient by inject()

    @Volatile
    private var currentPendingResult: PendingResult<AutocompletePredictionBuffer>? = null

    // Blocks thread
    fun getPredictions(
            constraint: CharSequence?,
            bounds: LatLngBounds,
            autocompleteFilter: AutocompleteFilter?)
            : ArrayList<LocationDetailed>? {
        if (constraint == null || constraint.isEmpty()) return null

        if (!googleApiClient.isConnected) {
            Log.e(TAG, "Google API client is not connected")
            throw Exception("Google API client is not connected")
        }

        Log.i(TAG, "Executing autocomplete query for: $constraint")

        if (currentPendingResult != null && currentPendingResult?.isCanceled == false) {
            currentPendingResult?.cancel()
        }

        val pendingResult = Places
                .GeoDataApi
                .getAutocompletePredictions(
                        googleApiClient,
                        constraint.toString(),
                        bounds,
                        autocompleteFilter)

        this.currentPendingResult = pendingResult

        val autocompletePredictions = pendingResult.await(3, TimeUnit.SECONDS)

        val status = autocompletePredictions.status
        if (!status.isSuccess) {
            autocompletePredictions.release()
            if (!status.isCanceled) {
                throw Exception("Error getting place predictions: " + status.toString())
            }
            return null
        } else {
            Log.i(TAG, "Received " + autocompletePredictions.count + " predictions.")

            val resultList = ArrayList<LocationDetailed>(autocompletePredictions.count)

            val iterator = autocompletePredictions.iterator()
            while (iterator.hasNext()) {
                val prediction = iterator.next()
                resultList.add(LocationDetailed(
                        prediction.getPrimaryText(null).toString(),
                        prediction.getSecondaryText(null).toString(),
                        prediction.placeId))
            }

            autocompletePredictions.release()

            return resultList
        }
    }
}