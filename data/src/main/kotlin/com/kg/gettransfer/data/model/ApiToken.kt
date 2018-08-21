package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiToken {
	@SerializedName("token")
	@Expose
	lateinit var token: String
}
