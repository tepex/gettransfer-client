package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiProfile(@SerializedName(ACCOUNT_EMAIL) @Expose var email: String? = null,
                 @SerializedName(ACCOUNT_PHONE) @Expose var phone: String? = null,
                 @SerializedName(ACCOUNT_FULL_NAME) @Expose var fullName: String? = null)
