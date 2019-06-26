package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.PriceEntity

data class PriceModel(
    @SerializedName(PriceEntity.BASE) @Expose val base: MoneyModel,
    @SerializedName(PriceEntity.NO_DISCOUNT) @Expose val withoutDiscount: MoneyModel?,
    @SerializedName(PriceEntity.PERCENTAGE_30) @Expose val percentage30: String,
    @SerializedName(PriceEntity.PERCENTAGE_70) @Expose val percentage70: String,
    @SerializedName(PriceEntity.AMOUNT) @Expose val amount: Double
)

fun PriceModel.map() = PriceEntity(base.map(), withoutDiscount?.map(), percentage30, percentage70, amount)

fun PriceEntity.map() = PriceModel(base.map(), withoutDiscount?.map(), percentage30, percentage70, amount)
