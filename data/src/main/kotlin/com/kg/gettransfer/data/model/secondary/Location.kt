package com.kg.gettransfer.data.model.secondary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Location(
        @Expose
        @SerializedName("name")
        var name: String = "",

        @Expose
        @SerializedName("point")
        var point: String = ""): RealmObject() {

    constructor(name: String, latitude: Double, longitude: Double): this(name, "($latitude},${longitude})")
}
