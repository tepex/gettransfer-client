package com.kg.gettransfer.views


import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.kg.gettransfer.data.LocationDetailed
import com.kg.gettransfer.modules.googleapi.GeoUtils
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


/**
 * Created by denisvakulenko on 09/02/2018.
 */


class LocationView : TextView, KoinComponent {
    private val geoUtils: GeoUtils by inject()

    private val disposable: Disposable

    var onLocationChanged: Runnable? = null


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    init {
        disposable = geoUtils.subscribe {
            if (it.placeID == location?.placeID && it.title == location?.title) {
                location = it
            }
        }
    }


    var location: LocationDetailed? = null
        set(newLocation) {
            if (newLocation?.equalsRaw(field) == true
                    && newLocation.latLng == null
                    && newLocation.placeID == null) return

            field = newLocation
            text = newLocation?.title ?: ""
            onLocationChanged?.run()

            if (newLocation?.valid == false) {
                geoUtils.validate(newLocation)
            }
        }
}