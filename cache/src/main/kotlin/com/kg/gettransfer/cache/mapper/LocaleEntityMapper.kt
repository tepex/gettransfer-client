package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.LocaleCached

import com.kg.gettransfer.data.model.LocaleEntity

class LocaleEntityMapper: EntityMapper<LocaleCached, LocaleEntity> {
    override fun fromCached(type: LocaleCached) = LocaleEntity(type.code, type.title)
    override fun toCached(type: LocaleEntity) = LocaleCached(type.code, type.title)
}
