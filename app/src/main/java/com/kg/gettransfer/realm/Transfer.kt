package com.kg.gettransfer.realm


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required


/**
 * Created by denisvakulenko on 29/01/2018.
 */


@RealmClass
open class Transfer : RealmObject() {
    @Expose
    @SerializedName("id")
    @PrimaryKey
    var id: Int = -1

    @Expose
    @SerializedName("from")
    var from: Location? = null
    @Expose
    @SerializedName("to")
    var to: Location? = null

    @Expose
    @SerializedName("duration")
    var duration: Int = 0
    @Expose
    @SerializedName("distance")
    var distance: Int = 0
    @Expose
    @SerializedName("time")
    var time: Int = 0

    @Expose
    @SerializedName("status")
    var status: String? = null

    @Expose
    @SerializedName("date_to")
    var dateTo: String = ""
    @Expose
    @SerializedName("time_return")
    var dateReturn: String = ""

    // --

    @Expose
    @SerializedName("pax")
    var pax: Int = 1

    @Expose
    @SerializedName("name_sign")
    var nameSign: String = ""

    @Expose
    @SerializedName("transport_types")
    @Required
    var transportTypes: RealmList<java.lang.Integer?>? = null

    @Expose
    @SerializedName("child_seats")
    var childSeats: String = ""

    @Expose
    @SerializedName("comments")
    var comments: String? = null

    // --

    @Expose
    @SerializedName("flight_number")
    var flightNumber: String? = null

    // --

    @Expose
    @SerializedName("offers_updated_at")
    var offersUpdatedAt: String? = null
}
