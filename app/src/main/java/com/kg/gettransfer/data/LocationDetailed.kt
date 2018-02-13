package com.kg.gettransfer.data

import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.models.Location


/**
 * Created by denisvakulenko on 09/02/2018.
 */

class LocationDetailed(
        val title: String,
        val subtitle: String?,
        val placeID: String?,
        val latLng: LatLng?) {


    constructor(title: String) : this(title, null, null, null)

    constructor(title: String, subtitle: String, placeID: String?)
            : this(title, subtitle, placeID, null)


    val valid: Boolean
        get() = title.isNotEmpty() && latLng != null


    fun toLocation(): Location = Location(title, latLng!!.latitude, latLng.longitude)


    fun equalsRaw(b: LocationDetailed?): Boolean = title == b?.title
}