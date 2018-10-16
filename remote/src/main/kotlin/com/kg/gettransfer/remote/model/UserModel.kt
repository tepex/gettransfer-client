package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserModel(fullName: String, email: String?, phone: String,
                @SerializedName("terms_accepted") @Expose val termsAccepted: Boolean = true): ProfileModel(fullName, email, phone)
