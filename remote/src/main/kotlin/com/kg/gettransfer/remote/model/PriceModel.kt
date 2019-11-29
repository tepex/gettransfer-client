package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.PriceEntity

data class PriceModel(
    @SerializedName(PriceEntity.BASE) @Expose val base: MoneyModel,
    @SerializedName(PriceEntity.NO_DISCOUNT) @Expose val withoutDiscount: MoneyModel?,
    @SerializedName(PriceEntity.AMOUNT) @Expose val amount: Double
)

fun PriceModel.map() = PriceEntity(base.map(), withoutDiscount?.map(), amount)

fun PriceEntity.map() = PriceModel(base.map(), withoutDiscount?.map(), amount)
