package com.kg.gettransfer.realm


import android.content.Context
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.R
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
    var price: Price? = null

    @Expose
    @SerializedName("status")
    var status: String? = null

    @Expose
    @SerializedName("carrier")
    var carrier: Carrier? = null

    @Expose
    @SerializedName("vehicle")
    var vehicle: Vehicle? = null

    @Expose
    @SerializedName("wifi")
    var wifi: Boolean? = null

    @Expose
    @SerializedName("refreshments")
    var refreshments: Boolean? = null

    fun facilities(c: Context): String? {
        var facilities = if (wifi == true) "WiFi" else null
        if (refreshments == true)
            if (facilities == null) facilities = c.getString(R.string.refreshments)
            else facilities += "    " + c.getString(R.string.refreshments)
        return facilities
    }
}