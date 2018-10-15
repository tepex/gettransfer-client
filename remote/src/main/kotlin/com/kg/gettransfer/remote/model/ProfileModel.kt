package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class ProfileModel(@SerializedName("full_name") @Expose val fullName: String?,
                        @SerializedName("email") @Expose val email: String?,
                        @SerializedName("phone") @Expose val phone: String?)
