package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.UserEntity

class UserModel(fullName: String, email: String?, phone: String,
                @SerializedName(UserEntity.TERMS_ACCEPTED) @Expose val termsAccepted: Boolean = true): ProfileModel(fullName, email, phone)
