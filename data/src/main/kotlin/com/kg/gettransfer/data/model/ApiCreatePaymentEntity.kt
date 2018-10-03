package com.kg.gettransfer.data.model

import com.google.gson.annotations.SerializedName

open class ApiCreatePaymentEntity(@SerializedName("transfer_id") val transferId: Long,
                                  @SerializedName("offer_id") val offerId: Long?,
                                  @SerializedName("gateway_id") val gatewayId: String,
                                  @SerializedName("percentage") val percentage: Int)