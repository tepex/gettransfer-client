package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.BraintreeTokenEntity

data class BraintreeTokenModel(
    @SerializedName(BraintreeTokenEntity.TOKEN)       @Expose val token: String,
    @SerializedName(BraintreeTokenEntity.ENVIRONMENT) @Expose val environment: String
)

fun BraintreeTokenEntity.map() = BraintreeTokenModel(token, environment)

fun BraintreeTokenModel.map() = BraintreeTokenEntity(token, environment)
