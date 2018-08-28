package com.kg.gettransfer.data.model

import com.google.gson.annotations.SerializedName

class ApiResponse<T> {
	@SerializedName("result")
	lateinit var result: String

    @SerializedName("data")
    var data: T? = null

    @SerializedName("error")
    var error: ApiError? = null
}

class ApiError {
	@SerializedName("type")
	lateinit var type: String

    @SerializedName("details")
    lateinit var details: String
}
