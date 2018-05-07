package com.kg.gettransfer.realm


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.realm.secondary.Currency
import com.kg.gettransfer.realm.secondary.Locale
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 28/02/2018.
 */


@RealmClass
open class Config(
        @PrimaryKey
        var id: Int = 0,

        @Expose
        @SerializedName("available_locales")
        var availableLocales: RealmList<Locale?>? = null,

        @Expose
        @SerializedName("preferred_locale")
        var preferredLocale: String? = null,

        @Expose
        @SerializedName("supported_currencies")
        var supportedCurrencies: RealmList<Currency?>? = null,

        @Expose
        @SerializedName("supported_distance_units")
        var supportedDistanceUnits: RealmList<String?>? = null)

    : RealmObject()