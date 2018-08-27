package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

const val TOKEN = "token"

class ApiToken(@SerializedName(TOKEN) @Expose val token: String)
