package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentStatusEventEntity(
    @SerialName(STATUS) val status: String
) {

    fun isPaymentSuccessed() = status == SUCCESSED_PAYMENT

    companion object {
        const val STATUS = "status"
        const val SUCCESSED_PAYMENT = "success"
    }
}
