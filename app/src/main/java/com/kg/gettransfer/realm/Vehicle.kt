package com.kg.gettransfer.realm


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 20/02/2018.
 */


@RealmClass
open class Vehicle : RealmObject() {
//    @Expose
//    @SerializedName("id")
//    @PrimaryKey
//    var id: Int = -1

    @Expose
    @SerializedName("name")
    var name: String? = null

    @Expose
    @SerializedName("completed_transfers")
    var year: Int = -1

    @Expose
    @SerializedName("transport_type_id")
    var transportTypeID: Int = -1
}