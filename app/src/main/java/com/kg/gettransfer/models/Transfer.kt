package com.kg.gettransfer.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 29/01/2018.
 */


@RealmClass
open class Transfer : RealmObject() {
    @Expose
    @SerializedName("from")
    var from: Location? = null
    @Expose
    @SerializedName("to")
    var to: Location? = null

    @Expose
    @SerializedName("distance")
    var distance: Int = 0
    @Expose
    @SerializedName("time")
    var time: Int = 0

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
    @SerializedName("id")
    @PrimaryKey
    var id: Int = -1
}
