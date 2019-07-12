package com.kg.gettransfer.domain.model

data class Partner(
    val balance: Balance,
    val creditLimit: Balance,
    val availableMoney: Balance
)
