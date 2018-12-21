package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.LocaleEntity

import java.util.Locale

/**
 * Map a [LocaleEntity] to and from a [Locale] instance when data is moving between
 * this later and the Domain layer
 */
open class LocaleMapper : Mapper<LocaleEntity, Locale> {
    override fun fromEntity(type: LocaleEntity) = Locale(type.code)
    override fun toEntity(type: Locale): LocaleEntity { throw UnsupportedOperationException() }
}
