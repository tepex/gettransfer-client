package com.kg.gettransfer.data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.network.PassengerProfile


/**
 * Created by denisvakulenko on 29/01/2018.
 */


//@RealmClass
class Transfer {
    //} : RealmObject {
    @Expose
    @SerializedName("distance")
    var distance: Int
    @Expose
    @SerializedName("time")
    var time: Int
    @Expose
    @SerializedName("from")
    var from: Map<String, String>
    @Expose
    @SerializedName("to")
    var to: Map<String, String>
    @Expose
    @SerializedName("date_to")
    var dateTo: String
    @Expose
    @SerializedName("time_to")
    var timeTo: String
    @Expose
    @SerializedName("transport_types")
    var transportTypes: IntArray
    @Expose
    @SerializedName("pax")
    var pax: Int
    @Expose
    @SerializedName("name_sign")
    var nameSign: String
    @Expose
    @SerializedName("passenger_profile")
    var passengerProfile: Map<String, Map<String, String>>
    @Expose
    @SerializedName("id")
//    @PrimaryKey
    var id: Int

    constructor(distance: Int, time: Int, from: Map<String, String>, to: Map<String, String>, dateTo: String, timeTo: String, transportTypes: IntArray, pax: Int, nameSign: String, passengerProfile: Map<String, Map<String, String>>, id: Int = -1) {
        this.distance = distance
        this.time = time
        this.from = from
        this.to = to
        this.dateTo = dateTo
        this.timeTo = timeTo
        this.transportTypes = transportTypes
        this.pax = pax
        this.nameSign = nameSign
        this.passengerProfile = passengerProfile
        this.id = id
    }

    constructor(distance: Int, time: Int, from: Location, to: Location, dateTo: String, timeTo: String, transportTypes: IntArray, pax: Int, nameSign: String, passengerProfile: PassengerProfile, id: Int = -1) {
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
        this.id = id
    }

    constructor() : this(
            0,
            0,
            Location("", 0.0, 0.0).toMap(),
            Location("", 0.0, 0.0).toMap(),
            "",
            "",
            intArrayOf(),
            0,
            "",
            PassengerProfile("", "").toMap(),
            -1
    )
}
