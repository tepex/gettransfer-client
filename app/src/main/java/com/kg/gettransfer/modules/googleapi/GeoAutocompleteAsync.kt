package com.kg.gettransfer.modules.googleapi


import android.content.Context
import android.util.Log
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.jakewharton.rxrelay2.PublishRelay
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 07/02/2018.
 */


@Deprecated("Unused")
class GeoAutocompleteAsync(val context: Context) : KoinComponent {
    //val mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null)

    var response: PublishRelay<AutocompletePredictionBufferResponse> = PublishRelay.create()

    private val LAT_LNG_BOUNDS_WORLD = LatLngBounds(
            LatLng(-90.0, -180.0),
            LatLng(90.0, 180.0))

    fun get(str: String) {
        val mGeoDataClient = Places.getGeoDataClient(context, null)

        val results = mGeoDataClient.getAutocompletePredictions(str, LAT_LNG_BOUNDS_WORLD, null)
        results.addOnCompleteListener {
            if (it.isSuccessful) response.accept(it.result)

            val autocompletePredictions = results.result

            //else response.accept(AutocompletePredictionBufferResponse())

            val iterator = autocompletePredictions.iterator()
//            val resultList = ArrayList(autocompletePredictions.getCount())
            while (iterator.hasNext()) {
                val prediction = iterator.next()
                Log.d("LA", prediction.getPrimaryText(null).toString() + "   " + prediction.getSecondaryText(null).toString())

//                resultList.add(PlaceAutocomplete(prediction.getPlaceId(),
//                        prediction.getFullText(null)))
            }
            // Buffer release
            autocompletePredictions.release()
        }
    }
}