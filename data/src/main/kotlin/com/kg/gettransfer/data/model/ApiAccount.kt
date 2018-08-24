package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiAccountWrapper(@SerializedName("account") @Expose var account: ApiAccount)

class ApiAccount(@SerializedName("email") @Expose var email: String,
                 @SerializedName("phone") @Expose var phone: String,
                 @SerializedName("locale") @Expose var locale: String,
                 @SerializedName("currency") @Expose var currency: String,
                 @SerializedName("distance_unit") @Expose var distanceUnit: String,
                 @SerializedName("full_name") @Expose var fullName: String,
                 @SerializedName("groups") @Expose var groups: Array<String>,
                 @SerializedName("terms_accepted") @Expose var termsAccepted: Boolean) 
