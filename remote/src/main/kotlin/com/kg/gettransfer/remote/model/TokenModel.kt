package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

const val TOKEN = "token"

class TokenModel(@SerializedName(TOKEN) @Expose val token: String)
