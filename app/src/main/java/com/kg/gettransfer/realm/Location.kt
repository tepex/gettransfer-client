package com.kg.gettransfer.realm


import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 29/01/2018.
 */


@RealmClass
open class Location(

        @Expose
        @SerializedName("name")
        var name: String = "",

        @Expose
        @SerializedName("point")
        var point: String = "")

    : RealmObject() {


    constructor(name: String, ll: LatLng) : this(name, "(${ll.latitude},${ll.longitude})")
}