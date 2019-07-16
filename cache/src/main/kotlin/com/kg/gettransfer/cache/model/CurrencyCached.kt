package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import com.kg.gettransfer.data.model.CurrencyEntity
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyCached(
    @ColumnInfo(name = CurrencyEntity.ISO_CODE) val code: String,
    @ColumnInfo(name = CurrencyEntity.SYMBOL) val symbol: String
)

@Serializable
data class CurrencyCachedList(val list: List<CurrencyCached>)

fun CurrencyCached.map() = CurrencyEntity(code, symbol)

fun CurrencyEntity.map() = CurrencyCached(code, symbol)
