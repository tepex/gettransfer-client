package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MoneyModel(@SerializedName("default") @Expose val default: String,
                 @SerializedName("preferred") @Expose val preferred: String?)
