package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiResponse<T> {
	@SerializedName("result")
	@Expose
	lateinit var result: String

    @SerializedName("data")
    @Expose
    var data: T? = null

    @SerializedName("error")
    @Expose
    var error: ApiError? = null
}

class ApiError {
	@SerializedName("type")
	@Expose
	lateinit var type: String

    @SerializedName("details")
    @Expose
    lateinit var details: String
}
