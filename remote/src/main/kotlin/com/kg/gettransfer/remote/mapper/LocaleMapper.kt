package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.LocaleEntity

import com.kg.gettransfer.remote.model.LocaleModel

/**
 * Map a [LocaleEntity] from a [LocaleModel] instance when data is moving between this later and the Data layer.
 */
open class LocaleMapper(): EntityMapper<LocaleModel, LocaleEntity> {
    override fun fromRemote(type: LocaleModel) = LocaleEntity(type.code, type.title)
    override fun toRemote(type: LocaleEntity): LocaleModel { throw UnsupportedOperationException() }
}
