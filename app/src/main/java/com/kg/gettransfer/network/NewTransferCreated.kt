package com.kg.gettransfer.network


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 29/01/2018.
 */


open class NewTransferCreated {
    @SerializedName("id")
    @Expose
    var id: Int = 0
}