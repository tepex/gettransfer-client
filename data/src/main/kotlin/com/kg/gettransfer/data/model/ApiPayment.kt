package com.kg.gettransfer.data.model

import com.google.gson.annotations.SerializedName

open class ApiPayment(@SerializedName("payment_id") val  paymentId: Long?,
                      @SerializedName("pg_order_id") val pgOrderId: Long?,
                      @SerializedName("without_redirect") val withoutRedirect: Boolean?)