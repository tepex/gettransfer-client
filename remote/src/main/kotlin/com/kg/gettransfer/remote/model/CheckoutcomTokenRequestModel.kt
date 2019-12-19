package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.CheckoutcomTokenRequestEntity

data class CheckoutcomTokenRequestModel(
    @SerializedName(CheckoutcomTokenRequestEntity.TYPE)         @Expose val type: String,
    @SerializedName(CheckoutcomTokenRequestEntity.NUMBER)       @Expose val number: String,
    @SerializedName(CheckoutcomTokenRequestEntity.EXPIRY_MONTH) @Expose val month: String,
    @SerializedName(CheckoutcomTokenRequestEntity.EXPIRY_YEAR)  @Expose val year: String,
    @SerializedName(CheckoutcomTokenRequestEntity.CVV)          @Expose val cvv: String
)

fun CheckoutcomTokenRequestEntity.map() =
    CheckoutcomTokenRequestModel(
        type,
        number,
        month,
        year,
        cvv
    )
