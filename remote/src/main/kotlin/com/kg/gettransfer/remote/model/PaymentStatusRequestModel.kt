package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaymentStatusRequestModel(@SerializedName("payment_id") @Expose val paymentId: Long?,
                                @SerializedName("pg_order_id") @Expose val pgOrderId: Long?,
                                @SerializedName("without_redirect") @Expose val withoutRedirect: Boolean?) {
    companion object {
        const val STATUS_SUCCESSFUL = "successful"
        const val STATUS_FAILED     = "failed"
    }
}
