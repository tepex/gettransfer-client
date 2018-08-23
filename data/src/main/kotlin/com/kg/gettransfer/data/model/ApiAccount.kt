package com.kg.gettransfer.data.model

import com.google.gson.annotations.SerializedName

data class ApiAccount(@SerializedName("email") var email: String,
                      @SerializedName("phone") var phone: String,
                      @SerializedName("locale") var locale: String,
                      @SerializedName("currency") var currency: String,
                      @SerializedName("distance_unit") var distanceUnit: String,
                      @SerializedName("full_name") var fullName: String,
                      @SerializedName("groups") var groups: List<String>,
                      @SerializedName("terms_accepted") var termsAccepted: Boolean)
