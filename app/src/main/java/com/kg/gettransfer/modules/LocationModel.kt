package com.kg.gettransfer.modules

import com.kg.gettransfer.data.LocationDetailed
import com.kg.gettransfer.modules.googleapi.GeoUtils
import org.koin.standalone.KoinComponent

/**
 * Created by denisvakulenko on 19/03/2018.
 */

class LocationModel(private val geoUtils: GeoUtils) : AsyncModel(), KoinComponent {
    var onLocationChanged: Runnable? = null


    init {
        disposables.add(geoUtils.subscribe {
            if (it.placeID == location?.placeID && it.title == location?.title) {
                location = it
            }
            busy = false
        })
    }


    var location: LocationDetailed? = null
        set(newLocation) {
            if (newLocation?.equalsRaw(field) == true
                    && newLocation.latLng == null
                    && newLocation.placeID == null) return

            field = newLocation
            onLocationChanged?.run()

            if (newLocation?.valid == false) {
                busy = true
                geoUtils.validate(newLocation)
            }
        }
}