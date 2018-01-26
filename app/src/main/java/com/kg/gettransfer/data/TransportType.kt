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

//    var pax_max: Int
//    var luggage_max: Int,
//    var title_en: String,
//    var image: String
}