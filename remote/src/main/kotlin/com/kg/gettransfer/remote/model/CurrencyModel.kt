package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.CurrencyEntity

data class CurrencyModel(
    @SerializedName(CurrencyEntity.ISO_CODE) @Expose val code: String,
    @SerializedName(CurrencyEntity.SYMBOL) @Expose val symbol: String
)

fun CurrencyModel.map() = CurrencyEntity(code, symbol)
