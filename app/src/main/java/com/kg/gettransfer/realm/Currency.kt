package com.kg.gettransfer.realm


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 07/03/2018.
 */


@RealmClass
open class Currency(
        @Expose
        @SerializedName("iso_code")
        var isoCode: String = "",

        @Expose
        @SerializedName("symbol")
        var symbol: String = "")

    : RealmObject()