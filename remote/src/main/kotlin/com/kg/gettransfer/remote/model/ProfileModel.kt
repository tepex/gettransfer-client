package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.ProfileEntity

open class ProfileModel(@SerializedName(ProfileEntity.FULL_NAME) @Expose var fullName: String?,
                        @SerializedName(ProfileEntity.EMAIL) @Expose var email: String?,
                        @SerializedName(ProfileEntity.PHONE) @Expose var phone: String?)
