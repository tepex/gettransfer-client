package com.kg.gettransfer.modules.http.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import io.realm.annotations.Required


/**
 * Created by denisvakulenko on 28/02/2018.
 */


@RealmClass
open class Config(
        @Expose
        @SerializedName("supported_currencies")
        @Required
        var supportedCurrencies: RealmList<String?>? = null,

        @Expose
        @SerializedName("supported_distance_units")
        @Required
        var supportedDistanceUnits: RealmList<String?>? = null)

    : RealmObject()