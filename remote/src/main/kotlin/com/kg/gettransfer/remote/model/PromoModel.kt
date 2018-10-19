package com.kg.gettransfer.remote.model

import com.google.gson.annotations.SerializedName

class PromoModel(@SerializedName("result") val result: String,
                 @SerializedName("data") val data: String)