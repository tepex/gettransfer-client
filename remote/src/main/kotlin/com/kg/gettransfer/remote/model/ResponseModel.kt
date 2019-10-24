package com.kg.gettransfer.remote.model

import com.google.gson.annotations.SerializedName

data class ResponseModel<out T>(
    @SerializedName("result")
    val result: String,
    @SerializedName("data")
    val data: T?,
    @SerializedName("error")
    val error: ErrorModel?
) {
    class ErrorModel(
        @SerializedName("type")
        val type: String,
        @SerializedName("details")
        val details: Any? = null /* String | JsonObject */
    )
}
