package com.kg.gettransfer.realm


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 20/02/2018.
 */


@RealmClass
open class PriceConverted : RealmObject() {
    @Expose
    @SerializedName("default")
    var defaultCurrency: String? = null

    @Expose
    @SerializedName("preferred")
    var preferredCurrency: String? = null

    override fun toString(): String =
            if (preferredCurrency == null) defaultCurrency ?: ""
            else "($defaultCurrency) $preferredCurrency"
}

