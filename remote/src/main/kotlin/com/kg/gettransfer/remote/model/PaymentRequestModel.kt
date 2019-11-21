package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.PaymentRequestEntity

data class PaymentRequestModel(
    @SerializedName(PaymentRequestEntity.TRANSFER_ID) @Expose val transferId: Long,
    @SerializedName(PaymentRequestEntity.OFFER_ID)    @Expose val offerId: Long?,
    @SerializedName(PaymentRequestEntity.GATEWAY_ID)  @Expose val gatewayId: String,
    @SerializedName(PaymentRequestEntity.PERCENTAGE)  @Expose val percentage: Int,
    @SerializedName(PaymentRequestEntity.BOOK_NOW_TRANSPORT_TYPE) @Expose val bookNowTransportType: String?
)

fun PaymentRequestEntity.map() =
    PaymentRequestModel(
        transferId,
        offerId,
        gatewayId,
        percentage,
        bookNowTransportType
    )
