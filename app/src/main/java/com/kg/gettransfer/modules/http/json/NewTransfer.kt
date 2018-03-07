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
    var from: Map<String, String> = mapOf()

    @Expose
    @SerializedName("to")
    var to: Map<String, String> = mapOf()

    @Expose
    @SerializedName("distance")
    var distance: Int = 0
    @Expose
    @SerializedName("time")
    var time: Int = 0

    // --

    @Expose
    @SerializedName("date_to")
    var dateTo: String = ""
    @Expose
    @SerializedName("time_to")
    var timeTo: String = ""

    @Expose
    @SerializedName("transport_types")
    var transportTypes: IntArray = intArrayOf()

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


    constructor(distance: Int, time: Int, from: Location, to: Location, dateTo: String, timeTo: String, transportTypes: IntArray, pax: Int, nameSign: String, passengerProfile: PassengerProfile) {
        this.distance = distance
        this.time = time
        this.from = from.toMap()
        this.to = to.toMap()
        this.dateTo = dateTo
        this.timeTo = timeTo
        this.transportTypes = transportTypes
        this.pax = pax
        this.nameSign = nameSign
        this.passengerProfile = passengerProfile.toMap()
    }

    @Deprecated
    constructor(from: Location, to: Location) {
        this.from = from.toMap()
        this.to = to.toMap()
    }

    constructor(distance: Int, time: Int, from: Location, to: Location) {
        this.distance = distance
        this.time = time
        this.from = from.toMap()
        this.to = to.toMap()
    }
}
