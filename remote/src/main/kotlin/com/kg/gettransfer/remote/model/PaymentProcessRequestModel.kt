package com.kg.gettransfer.remote.model

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.PaymentProcessRequestEntity

open class PaymentProcessRequestModel(
    @SerializedName(PaymentProcessRequestEntity.PAYMENT_ID) @Expose val paymentId: Long
)

class StringPaymentProcessRequestModel(
    paymentId: Long,
    @SerializedName(PaymentProcessRequestEntity.TOKEN) @Expose val token: String
) : PaymentProcessRequestModel(
    paymentId
)

class JsonPaymentProcessRequestModel(
    paymentId: Long,
    @SerializedName(PaymentProcessRequestEntity.TOKEN) @Expose val token: JsonObject
) : PaymentProcessRequestModel(
     paymentId
)

fun PaymentProcessRequestEntity.map(token: String) =
    StringPaymentProcessRequestModel(
        paymentId,
        token
    )

fun PaymentProcessRequestEntity.map(token: JsonObject) =
    JsonPaymentProcessRequestModel(
        paymentId,
        token
    )
