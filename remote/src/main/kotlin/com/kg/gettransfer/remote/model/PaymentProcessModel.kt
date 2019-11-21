package com.kg.gettransfer.remote.model

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.PaymentProcessEntity

data class PaymentProcessModel(
    @SerializedName(PaymentProcessEntity.PAYMENT_ID) @Expose val paymentId: Long,
    @SerializedName(PaymentProcessEntity.TOKEN)      @Expose val token: JsonObject
)

fun PaymentProcessEntity.map() =
    PaymentProcessModel(
        paymentId,
        JsonParser().parse(token).asJsonObject
    )