package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.CheckoutcomTokenEntity

data class CheckoutcomTokenModel(
    @SerializedName(CheckoutcomTokenEntity.TOKEN)       @Expose val token: String?,
    @SerializedName(CheckoutcomTokenEntity.ERROR_TYPE)  @Expose val errorType: String?,
    @SerializedName(CheckoutcomTokenEntity.ERROR_CODES) @Expose val errorCodes: List<String>?
)

fun CheckoutcomTokenModel.map() =
    CheckoutcomTokenEntity(
        token,
        errorType,
        errorCodes
    )
