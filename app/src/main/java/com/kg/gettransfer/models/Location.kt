package com.kg.gettransfer.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class Location(
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("point")
        val point: String) {

    constructor(name: String, la: Double, lo: Double) : this(name, "(" + la.toString() + "," + lo.toString() + ")")

    fun toMap(): Map<String, String> {
        return mapOf("name" to name, "point" to point)
    }
}