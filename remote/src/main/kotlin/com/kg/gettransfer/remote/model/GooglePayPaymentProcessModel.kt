package com.kg.gettransfer.remote.model

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.GooglePayPaymentProcessEntity

data class GooglePayPaymentProcessModel(
    @SerializedName(GooglePayPaymentProcessEntity.PAYMENT_ID) @Expose val paymentId: String,
    @SerializedName(GooglePayPaymentProcessEntity.TOKEN) @Expose val token: JsonObject
)

fun GooglePayPaymentProcessEntity.map() =
    GooglePayPaymentProcessModel(
        paymentId,
        JsonParser().parse(token).asJsonObject
    )