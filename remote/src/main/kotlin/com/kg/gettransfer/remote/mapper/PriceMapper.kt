package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PriceEntity

import com.kg.gettransfer.remote.model.PriceModel

/**
 * Map a [PriceModel] from an [PriceEntity] instance when data is moving between this later and the Data layer.
 */
open class PriceMapper(private val moneyMapper: MoneyMapper): EntityMapper<PriceModel, PriceEntity> {

    override fun fromRemote(type: PriceModel) =
        PriceEntity(moneyMapper.fromRemote(type.base), type.percentage30, type.percentage70, type.amount) 
    override fun toRemote(type: PriceEntity) = 
        PriceModel(moneyMapper.toRemote(type.base), type.percentage30, type.percentage70, type.amount)
}
