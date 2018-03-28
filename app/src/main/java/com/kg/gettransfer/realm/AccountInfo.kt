package com.kg.gettransfer.realm


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*


/**
 * Created by denisvakulenko on 28/03/2018.
 */


@RealmClass
open class AccountInfo(
        @Expose
        @SerializedName("email")
        @PrimaryKey
        var email: String? = null,

        @Expose
        @SerializedName("phone")
        var phone: String? = null,

        @Expose
        @SerializedName("locale")
        var locale: String? = null,

        @Expose
        @SerializedName("currency")
        var currencyUnit: String? = null,

        @Expose
        @SerializedName("distance_unit")
        var distanceUnit: String? = null,

        var dateUpdated: Date? = null)

    : RealmObject()