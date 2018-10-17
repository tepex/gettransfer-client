package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaymentRequestModel(@SerializedName("transfer_id") @Expose val transferId: Long,
                          @SerializedName("offer_id")    @Expose val offerId: Long?,
                          @SerializedName("gateway_id")  @Expose val gatewayId: String,
                          @SerializedName("percentage")  @Expose val percentage: Int)
