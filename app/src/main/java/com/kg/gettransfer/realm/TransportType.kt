package com.kg.gettransfer.realm


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 25/01/2018.
 */


@RealmClass
open class TransportType(
        @PrimaryKey
        @SerializedName("id")
        @Expose
        var id: String = "",

        var title: String = "",

        @SerializedName("pax_max")
        @Expose
        var paxMax: Int = 0,

        @SerializedName("luggage_max")
        @Expose
        var luggageMax: Int = 0)

    : RealmObject()