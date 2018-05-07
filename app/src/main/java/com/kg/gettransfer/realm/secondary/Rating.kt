package com.kg.gettransfer.realm.secondary


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 01/03/2018.
 */


@RealmClass
open class Rating(
        @Expose
        @SerializedName("average")
        var average: Int = 0,

        @Expose
        @SerializedName("vehicle")
        var vehicle: Int = 0,

        @Expose
        @SerializedName("drive")
        var drive: Int = 0,

        @Expose
        @SerializedName("fair")
        var fair: Int = 0)

    : RealmObject()