package com.kg.gettransfer.remote.model

import com.kg.gettransfer.data.model.PaymentEntity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PaymentModel(
    @SerializedName(PaymentEntity.TYPE) @Expose val type: String,
    @SerializedName(PaymentEntity.URL) @Expose val url: String?,
    @SerializedName(PaymentEntity.PAYMENT_ID) @Expose val id: Long?
)
