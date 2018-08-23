package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ApiToken(@SerializedName("token") @Expose var token: String)
