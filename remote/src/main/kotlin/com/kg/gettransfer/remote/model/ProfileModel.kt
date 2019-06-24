package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.ProfileEntity

open class ProfileModel(
    @SerializedName(ProfileEntity.FULL_NAME) @Expose val fullName: String?,
    @SerializedName(ProfileEntity.EMAIL) @Expose val email: String?,
    @SerializedName(ProfileEntity.PHONE) @Expose val phone: String?
)

fun ProfileModel.map() = ProfileEntity(fullName, email, phone)

fun ProfileEntity.map() = ProfileModel(fullName, email, phone)
