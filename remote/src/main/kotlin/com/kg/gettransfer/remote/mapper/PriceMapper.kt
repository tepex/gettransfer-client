package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PriceEntity

import com.kg.gettransfer.remote.model.PriceModel

import org.koin.standalone.inject

/**
 * Map a [PriceModel] from a [PriceEntity] instance when data is moving between this later and the Data layer.
 */
open class PriceMapper: EntityMapper<PriceModel, PriceEntity> {
    private val moneyMapper: MoneyMapper by inject()
    
    override fun fromRemote(type: PriceModel) =
        PriceEntity(moneyMapper.fromRemote(type.base), type.withoutDiscount?.let { moneyMapper.fromRemote(it) }, type.percentage30, type.percentage70, type.amount)
    override fun toRemote(type: PriceEntity) = 
        PriceModel(moneyMapper.toRemote(type.base), type.withoutDiscount?.let { moneyMapper.toRemote(it) }, type.percentage30, type.percentage70, type.amount)
}
