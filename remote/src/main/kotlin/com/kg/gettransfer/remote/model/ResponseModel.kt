package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseModel<T> {
    @SerializedName("result")
    @Expose
    lateinit var result: String

    @SerializedName("data")
    @Expose
    var data: T? = null

    @SerializedName("error")
    @Expose
    var error: ErrorModel? = null
}

class ErrorModel {
    @Expose
    @SerializedName("type")
    lateinit var type: String

    @Expose
    @SerializedName("details")
    var details: Any? = null /* String | JsonObject */
}
