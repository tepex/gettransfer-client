package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.PaymentStatusRequestEntity

data class PaymentStatusRequestModel(
    @SerializedName(PaymentStatusRequestEntity.PAYMENT_ID) @Expose val paymentId: Long?,
    @SerializedName(PaymentStatusRequestEntity.PG_ORDER_ID) @Expose val pgOrderId: Long?,
    @SerializedName(PaymentStatusRequestEntity.WITHOUT_REDIRECT) @Expose val withoutRedirect: Boolean?
) {

    companion object {
        const val STATUS_SUCCESSFUL = "successful"
        const val STATUS_FAILED     = "failed"
    }
}

fun PaymentStatusRequestEntity.map() = PaymentStatusRequestModel(paymentId, pgOrderId, withoutRedirect)
