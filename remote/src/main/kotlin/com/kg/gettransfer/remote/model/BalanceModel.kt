package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.BalanceEntity

data class BalanceModel(
    @SerializedName(BalanceEntity.AMOUNT) @Expose val amount: Double,
    @SerializedName(BalanceEntity.DEFAULT) @Expose val default: String
)

fun BalanceModel.map() = BalanceEntity(amount, default)
