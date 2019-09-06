package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Partner

data class PartnerEntity(
    val balance: BalanceEntity,
    val creditLimit: BalanceEntity,
    val availableMoney: BalanceEntity,
    val defaultPromoCode: String?
) {

    companion object {
        const val BALANCE            = "balance"
        const val CREDIT_LIMIT       = "credit_limit"
        const val AVAILABLE_MONEY    = "available_money"
        const val DEFAULT_PROMO_CODE = "default_promo_code"
    }
}

fun PartnerEntity.map() =
    Partner(
        balance.map(),
        creditLimit.map(),
        availableMoney.map(),
        defaultPromoCode
    )
