package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaymentStatusWrapperModel(@SerializedName("payment") @Expose val payment: PaymentStatusModel)

class PaymentStatusModel(@SerializedName("id") @Expose val id: Long,
                         @SerializedName("status") @Expose val status: String)
