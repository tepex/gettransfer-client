package com.kg.gettransfer.data.model

import com.google.gson.annotations.SerializedName

data class PaymentPureEntity(@SerializedName("id") val id: Long,
                             @SerializedName("status") val status: String)