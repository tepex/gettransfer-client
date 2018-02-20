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
open class Carrier : RealmObject() {
    @Expose
    @SerializedName("id")
    @PrimaryKey
    var id: Int = -1

    @Expose
    @SerializedName("title")
    var title: String? = null

    @Expose
    @SerializedName("approved")
    var approved: Boolean? = null

    @Expose
    @SerializedName("completed_transfers")
    var completedTransfers: Int = 0
}