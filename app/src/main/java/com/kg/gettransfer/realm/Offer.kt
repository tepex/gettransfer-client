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
open class Offer : RealmObject() {
    @Expose
    @SerializedName("id")
    @PrimaryKey
    var id: Int = -1

    @Expose
    @SerializedName("price")
    var price: String? = null
    @Expose
    @SerializedName("status")
    var status: String? = null

    @Expose
    @SerializedName("carrier")
    var carrier: Carrier? = null

    @Expose
    @SerializedName("vehicle")
    var vehicle: Vehicle? = null
}