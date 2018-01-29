package com.kg.gettransfer.data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


/**
 * Created by denisvakulenko on 25/01/2018.
 */


open class TransportType : RealmObject() {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("title_ru")
    @Expose
    var title: String = ""

    @SerializedName("pax_max")
    @Expose
    var paxMax: Int = 0

    @SerializedName("luggage_max")
    @Expose
    var luggageMax: Int = 0

    @SerializedName("image")
    @Expose
    var imageUrl: String = ""
}