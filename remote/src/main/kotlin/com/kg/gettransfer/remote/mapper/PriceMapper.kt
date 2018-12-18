package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PriceEntity

import com.kg.gettransfer.remote.model.PriceModel

import org.koin.standalone.get

/**
 * Map a [PriceModel] from a [PriceEntity] instance when data is moving between this later and the Data layer.
 */
open class PriceMapper : EntityMapper<PriceModel, PriceEntity> {
    private val moneyMapper = get<MoneyMapper>()

    override fun fromRemote(type: PriceModel) =
        PriceEntity(
            base = moneyMapper.fromRemote(type.base),
            withoutDiscount = type.withoutDiscount?.let { moneyMapper.fromRemote(it) },
            percentage30 = type.percentage30,
            percentage70 = type.percentage70,
            amount = type.amount
        )

    override fun toRemote(type: PriceEntity) =
        PriceModel(
            base = moneyMapper.toRemote(type.base),
            withoutDiscount = type.withoutDiscount?.let { moneyMapper.toRemote(it) },
            percentage30 = type.percentage30,
            percentage70 = type.percentage70,
            amount = type.amount
        )
}
