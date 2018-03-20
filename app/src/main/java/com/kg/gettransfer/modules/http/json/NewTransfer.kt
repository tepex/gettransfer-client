package com.kg.gettransfer.modules.http.json;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kg.gettransfer.realm.Location


/**
 * Created by denisvakulenko on 02/02/2018.
 */


class NewTransfer {
    @Expose
    @SerializedName("from")
    var from: Location? = null
    @Expose
    @SerializedName("to")
    var to: Location? = null

    @Expose
    @SerializedName("distance")
    var routeDistance: Int? = null
    @Expose
    @SerializedName("time")
    var routeDuration: Int? = null // in minutes

    @Expose
    @SerializedName("duration")
    var hireDuration: Int? = null // in hours

    // --

    @Expose
    @SerializedName("trip_to")
    var dateTo: Trip? = null

    @Expose
    @SerializedName("trip_return")
    var dateReturn: Trip? = null

    @Expose
    @SerializedName("transport_types")
    var transportTypes: Array<String>? = null

    @Expose
    @SerializedName("pax")
    var pax: Int = 1

    // --

    @Expose
    @SerializedName("name_sign")
    var nameSign: String = ""
    @Expose
    @SerializedName("passenger_offered_price")
    var offeredPrice: Int? = null
    @Expose
    @SerializedName("flight_number")
    var flightNumber: String = ""
    @Expose
    @SerializedName("child_seats")
    var childSeats: Int? = null
    @Expose
    @SerializedName("comment")
    var comment: String = ""

    // --

    @Expose
    @SerializedName("passenger_profile")
    var passengerProfile: Map<String, Map<String, String>> = mapOf()

    // --

    @Expose
    @SerializedName("promo_code")
    var promoCode: String = ""


    // --


    constructor(from: Location, to: Location, routeDistance: Int, routeDuration: Int) {
        this.from = from
        this.to = to
        this.routeDistance = routeDistance
        this.routeDuration = routeDuration
    }

    constructor(from: Location, hireDuration: Int) {
        this.from = from
        this.hireDuration = hireDuration
    }
}
