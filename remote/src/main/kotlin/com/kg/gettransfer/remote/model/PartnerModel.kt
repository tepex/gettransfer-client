package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.PartnerEntity

data class PartnerModel(
    @SerializedName(PartnerEntity.BALANCE) @Expose val balance: BalanceModel,
    @SerializedName(PartnerEntity.CREDIT_LIMIT) @Expose val creditLimit: BalanceModel,
    @SerializedName(PartnerEntity.AVAILABLE_MONEY) @Expose val availableMoney: BalanceModel,
    @SerializedName(PartnerEntity.DEFAULT_PROMO_CODE) @Expose val defaultPromoCode: String?
)

fun PartnerModel.map() =
    PartnerEntity(
        balance.map(),
        creditLimit.map(),
        availableMoney.map(),
        defaultPromoCode
    )
