package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RoleModel(@SerializedName("role") @Expose val role: String)
