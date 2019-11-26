package com.kg.gettransfer.remote.model

import com.google.gson.JsonObject
import com.google.gson.JsonParser
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

fun PaymentProcessRequestEntity.mapString() =
    StringPaymentProcessRequestModel(
        paymentId,
        token
    )

fun PaymentProcessRequestEntity.mapJson() =
    JsonPaymentProcessRequestModel(
        paymentId,
        JsonParser().parse(token).asJsonObject
    )
