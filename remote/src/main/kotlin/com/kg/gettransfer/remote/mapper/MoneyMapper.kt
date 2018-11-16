package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.MoneyEntity

import com.kg.gettransfer.remote.model.MoneyModel

/**
 * Map a [MoneyModel] from an [MoneyEntity] instance when data is moving between this later and the Data layer.
 */
open class MoneyMapper(): EntityMapper<MoneyModel, MoneyEntity> {
    override fun fromRemote(type: MoneyModel) = MoneyEntity(type.def, type.preferred) 
    override fun toRemote(type: MoneyEntity) = MoneyModel(type.def, type.preferred)
}
