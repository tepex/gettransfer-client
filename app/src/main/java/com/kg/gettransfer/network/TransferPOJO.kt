package com.kg.gettransfer.network


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransferPOJO {
    @Expose
    @SerializedName("distance")
    val distance: Int
    @Expose
    @SerializedName("time")
    val time: Int
    @Expose
    @SerializedName("from")
    val from: Map<String, String>
    @Expose
    @SerializedName("to")
    val to: Map<String, String>
    @Expose
    @SerializedName("date_to")
    val dateTo: String
    @Expose
    @SerializedName("time_to")
    val timeTo: String
    @Expose
    @SerializedName("transport_types")
    val transportTypes: String
    @Expose
    @SerializedName("pax")
    val pax: Int
    @Expose
    @SerializedName("nameSign")
    val namSign: String
    @Expose
    @SerializedName("passenger_profile")
    val passengerProfile: Map<String, Map<String, String>>


    constructor(distance: Int, time: Int, from: Map<String, String>, to: Map<String, String>, dateTo: String, timeTo: String, transportTypes: String, pax: Int, namSign: String, passengerProfile: Map<String, Map<String, String>>) {
        this.distance = distance
        this.time = time
        this.from = from
        this.to = to
        this.dateTo = dateTo
        this.timeTo = timeTo
        this.transportTypes = transportTypes
        this.pax = pax
        this.namSign = namSign
        this.passengerProfile = passengerProfile
    }
}
