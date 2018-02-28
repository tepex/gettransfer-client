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
open class Price : RealmObject() {
    @Expose
    @SerializedName("base")
    var base: PriceConverted? = null

    @Expose
    @SerializedName("percentage_30")
    var p30: String? = null

    @Expose
    @SerializedName("percentage_70")
    var p70: String? = null

    @Expose
    @SerializedName("without_discount")
    var withoutDiscount: PriceConverted? = null

    @Expose
    @SerializedName("amount")
    var amount: Int = -1

    override fun toString(): String = base.toString()
}

