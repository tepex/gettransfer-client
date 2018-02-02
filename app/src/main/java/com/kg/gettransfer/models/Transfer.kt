package com.kg.gettransfer.models


import android.support.annotation.IntegerRes
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
    @SerializedName("distance")
    var distance: Int = 0
    @Expose
    @SerializedName("time")

    var time: Int = 0
    @Expose
    @SerializedName("from")
    var from: Location? = Location("", 0.0, 0.0)
    @Expose
    @SerializedName("to")
    var to: Location? = Location("", 0.0, 0.0)

    @Expose
    @SerializedName("date_to")
    var dateTo: String = ""
    @Expose
    @SerializedName("time_to")
    var timeTo: String = ""

    // TODO: Блин, что-то я не осилил как поддержать лист интов
//    @Expose
//    @SerializedName("transport_types")
//    @Required
//    var transportTypes: RealmList<Integer?>? = RealmList()
    @Expose
    @SerializedName("pax")
    var pax: Int = 1
    @Expose
    @SerializedName("name_sign")
    var nameSign: String = ""

    @Expose
    @SerializedName("passenger_profile")
    var passengerProfile: PassengerProfile? = PassengerProfile("", "")

    @Expose
    @SerializedName("id")
    @PrimaryKey
    var id: Int = -1
}
