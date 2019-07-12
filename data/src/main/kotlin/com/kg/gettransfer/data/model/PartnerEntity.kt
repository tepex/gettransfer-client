package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Partner

data class PartnerEntity(
    val balance: BalanceEntity,
    val creditLimit: BalanceEntity,
    val availableMoney: BalanceEntity
) {

    companion object {
        const val BALANCE         = "balance"
        const val CREDIT_LIMIT    = "credit_limit"
        const val AVAILABLE_MONEY = "available_money"
    }
}

fun PartnerEntity.map() = Partner(balance.map(), creditLimit.map(), availableMoney.map())
