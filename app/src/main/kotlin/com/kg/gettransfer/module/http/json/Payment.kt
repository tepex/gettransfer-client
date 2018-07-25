package com.kg.gettransfer.module.http.json


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 07/03/2018.
 */


class Payment {
    @Expose
    @SerializedName("type")
    var type: String? = null
    @Expose
    @SerializedName("url")
    var url: String? = null
}