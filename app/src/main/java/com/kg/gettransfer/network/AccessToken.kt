package com.kg.gettransfer.network


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 29/01/2018.
 */


open class AccessToken {
    @SerializedName("token")
    @Expose
    var token: String = ""
}