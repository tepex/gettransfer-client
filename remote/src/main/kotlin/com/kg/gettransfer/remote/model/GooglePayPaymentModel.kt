package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.GooglePayPaymentEntity

data class WrappedGooglePayPaymentModel(
    @SerializedName(GooglePayPaymentEntity.PARAMS) @Expose val params: GooglePayPaymentModel
)

data class GooglePayPaymentModel(
    @SerializedName(GooglePayPaymentEntity.PAYMENT_ID) @Expose val paymentId: String?,
    @SerializedName(GooglePayPaymentEntity.AMOUNT) @Expose val amount: Float?,
    @SerializedName(GooglePayPaymentEntity.CURRENCY) @Expose val currency: String?
)

fun GooglePayPaymentModel.map() =
    GooglePayPaymentEntity(
        paymentId,
        amount,
        currency
    )