package com.kg.gettransfer.data.model

import com.google.gson.annotations.SerializedName

open class ApiPaymentResult(@SerializedName("type") val type: String,
                      @SerializedName("url") val url: String?)