package com.kg.gettransfer.remote.model

import com.kg.gettransfer.data.model.PaymentEntity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.ParamsEntity

data class PaymentModel(
    @SerializedName(PaymentEntity.TYPE) @Expose val type: String,
    @SerializedName(PaymentEntity.URL) @Expose val url: String?,
    @SerializedName(PaymentEntity.PAYMENT_ID) @Expose val id: Long?,
    @SerializedName(PaymentEntity.PARAMS) @Expose val params: Params?
)

data class Params(@SerializedName(ParamsEntity.AMOUNT) @Expose val amount: String,
                  @SerializedName(ParamsEntity.CURRENCY) @Expose val currency: String,
                  @SerializedName(ParamsEntity.PAYMENT_ID) @Expose val paymentId: Long)
