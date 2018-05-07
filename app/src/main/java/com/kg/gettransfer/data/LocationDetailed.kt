package com.kg.gettransfer.data


import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.realm.secondary.Location


/**
 * Created by denisvakulenko on 09/02/2018.
 */


class LocationDetailed(
        val title: String,
        @Deprecated("Should be used in future")
        val subtitle: String?,
        val placeID: String?,
        val latLng: LatLng?,
        var validationSuccess: Boolean? = null,
        var myLocation: Boolean = false) {


    constructor(title: String)
            : this(title, null, null, null)

    constructor(title: String, subtitle: String, placeID: String?)
            : this(title, subtitle, placeID, null)

    constructor(latLng: LatLng, myLocation: Boolean)
            : this("", null, null, latLng, null, myLocation)


    fun isValid(): Boolean = title.isNotEmpty() && latLng != null

    fun isEmpty(): Boolean = title.isEmpty() && placeID == null && latLng == null

    fun toLocation(): Location? =
            if (latLng == null) null
            else Location(title, latLng)


    override fun equals(other: Any?): Boolean {
        if (other is LocationDetailed) {
            return title == other.title && subtitle == other.subtitle &&
                    placeID == other.placeID && latLng == other.latLng
        }
        return super.equals(other)
    }
}