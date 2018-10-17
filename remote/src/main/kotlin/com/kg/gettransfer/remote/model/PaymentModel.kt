package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaymentModel(@SerializedName("type") @Expose val type: String, @SerializedName("url") @Expose val url: String?)
