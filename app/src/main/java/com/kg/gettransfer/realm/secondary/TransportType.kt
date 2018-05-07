package com.kg.gettransfer.realm.secondary


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
        @Expose
        @PrimaryKey
        @SerializedName("id")
        var id: String = "",

        var title: String = "",

        @Expose
        @SerializedName("pax_max")
        var paxMax: Int = 0,

        @Expose
        @SerializedName("luggage_max")
        var luggageMax: Int = 0)

    : RealmObject()