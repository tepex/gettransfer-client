package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Balance

class BalanceEntity(
    val amount: Double,
    val default: String
) {

    companion object {
        const val AMOUNT  = "amount"
        const val DEFAULT = "default"
    }
}

fun BalanceEntity.map() = Balance(amount, default)
