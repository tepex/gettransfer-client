package com.kg.gettransfer.data


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by denisvakulenko on 25/01/2018.
 */


open class Response<T> {
    @SerializedName("result")
    @Expose
    var result: String = ""

    @SerializedName("data")
    @Expose
    var data: T? = null
}