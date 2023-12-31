package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.PaymentStatusEntity

data class PaymentStatusWrappedModel(
    @SerializedName(PaymentStatusEntity.ENTITY_NAME) @Expose val payment: PaymentStatusModel
)

data class PaymentStatusModel(
    @SerializedName(PaymentStatusEntity.ID) @Expose val id: Long,
    @SerializedName(PaymentStatusEntity.STATUS) @Expose val status: String
)

fun PaymentStatusModel.map() = PaymentStatusEntity(id, status)

fun PaymentStatusEntity.map() = PaymentStatusModel(id, status)
