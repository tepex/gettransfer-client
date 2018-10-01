package com.kg.gettransfer.remote.model

import com.google.gson.annotations.SerializedName

class ResponseModel<T> {
	@SerializedName("result")
	lateinit var result: String

    @SerializedName("data")
    var data: T? = null

    @SerializedName("error")
    var error: ErrorModel? = null
}

class ErrorModel {
	@SerializedName("type")
	lateinit var type: String

    @SerializedName("details")
    lateinit var details: Any /* String | JsonObject */
}
