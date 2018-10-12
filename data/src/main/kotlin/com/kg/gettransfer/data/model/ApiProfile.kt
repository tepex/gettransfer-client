package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class ApiProfile(@SerializedName(ACCOUNT_EMAIL) @Expose val email: String? = null,
                      @SerializedName(ACCOUNT_PHONE) @Expose val phone: String? = null,
                      @SerializedName(ACCOUNT_FULL_NAME) @Expose val fullName: String? = null)

class ApiUser(email: String?, phone: String?, fullName: String?,
              @SerializedName(ACCOUNT_TERMS_ACCEPTED) @Expose val termsAccepted: Boolean = true): ApiProfile(email, phone, fullName)
