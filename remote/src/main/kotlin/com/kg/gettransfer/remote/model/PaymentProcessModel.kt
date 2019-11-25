package com.kg.gettransfer.remote.model

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.PaymentProcessEntity

open class PaymentProcessModel(
    @SerializedName(PaymentProcessEntity.PAYMENT_ID) @Expose val paymentId: Long
)

class StringPaymentProcessModel(
    paymentId: Long,
    @SerializedName(PaymentProcessEntity.TOKEN) @Expose val token: String
) : PaymentProcessModel(
    paymentId
)

class JsonPaymentProcessModel(
    paymentId: Long,
    @SerializedName(PaymentProcessEntity.TOKEN) @Expose val token: JsonObject?
) : PaymentProcessModel(
     paymentId
)

fun PaymentProcessEntity.map() =
    if (isStringToken) {
        StringPaymentProcessModel(
            paymentId,
            token
        )
    } else {
        JsonPaymentProcessModel(
            paymentId,
            JsonParser().parse(token).asJsonObject
        )
    }
