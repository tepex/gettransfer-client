package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PromoModel(@SerializedName("result") @Expose val result: String,
                 @SerializedName("data") @Expose val data: String)