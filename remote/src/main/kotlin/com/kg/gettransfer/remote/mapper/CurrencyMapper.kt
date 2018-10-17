package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CurrencyEntity

import com.kg.gettransfer.remote.model.CurrencyModel

/**
 * Map a [CurrencyEntity] from a [CurrencyModel] instance when data is moving between this later and the Data layer.
 */
open class CurrencyMapper(): EntityMapper<CurrencyModel, CurrencyEntity> {
    override fun fromRemote(type: CurrencyModel) = CurrencyEntity(type.code, type.symbol)
    override fun toRemote(type: CurrencyEntity): CurrencyModel { throw UnsupportedOperationException() }
}
