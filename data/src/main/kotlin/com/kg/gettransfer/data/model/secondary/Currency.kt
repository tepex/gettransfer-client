package com.kg.gettransfer.data.model.secondary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Currency(
        @Expose
        @SerializedName("iso_code")
        var isoCode: String = "",

        @Expose
        @SerializedName("symbol")
        var symbol: String = ""): RealmObject()
