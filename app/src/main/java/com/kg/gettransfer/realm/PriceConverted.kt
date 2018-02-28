package com.kg.gettransfer.realm


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import io.realm.annotations.Required


/**
 * Created by denisvakulenko on 20/02/2018.
 */


@RealmClass
open class PriceConverted : RealmObject() {
    @Expose
    @SerializedName("default")
    var default: String = ""

    @Expose
    @SerializedName("preferred")
    var preferred: String? = null

    override fun toString(): String = (preferred ?: "") + " " + default
}

