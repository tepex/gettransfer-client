package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Response<T> {
	@SerializedName("result")
	@Expose
	lateinit var result: String

    @SerializedName("data")
    @Expose
    var data: T? = null
}
