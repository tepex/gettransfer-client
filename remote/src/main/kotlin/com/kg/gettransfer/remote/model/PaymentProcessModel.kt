package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.PaymentProcessEntity

data class PaymentProcessModel(
    @SerializedName(PaymentProcessEntity.PAYMENT)  @Expose val payment: PaymentStatusModel,
    @SerializedName(PaymentProcessEntity.REDIRECT) @Expose val redirect: String?
)

fun PaymentProcessModel.map() =
    PaymentProcessEntity(
        payment.map(),
        redirect
    )
