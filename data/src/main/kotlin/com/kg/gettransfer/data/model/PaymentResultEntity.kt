package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PaymentResultEntity(@SerializedName("type") val type: String, @SerializedName("url") val url: String?)

//data class ChangingStatusPayment(@SerializedName("payment") @Expose val payment: ApiPayment)