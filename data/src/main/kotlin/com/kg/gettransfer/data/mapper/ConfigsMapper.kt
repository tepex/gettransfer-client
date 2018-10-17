package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.ConfigsEntity

import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

/**
 * Map a [ConfigsEntity] to and from a [Configs] instance when data is moving between this later and the Domain layer.
 */
open class ConfigsMapper(private val transportTypeMapper: TransportTypeMapper,
                         private val paypalCredentialsMapper: PaypalCredentialsMapper,
                         private val localeMapper: LocaleMapper,
                         private val currencyMapper: CurrencyMapper,
                         private val cardGatewaysMapper: CardGatewaysMapper): Mapper<ConfigsEntity, Configs> {
    /**
     * Map a [ConfigsEntity] instance to a [Configs] instance
     */
    override fun fromEntity(type: ConfigsEntity): Configs {
        val locales = type.availableLocales.map { localeMapper.fromEntity(it) }
        return Configs(type.transportTypes.map { transportTypeMapper.fromEntity(it) },
                paypalCredentialsMapper.fromEntity(type.paypalCredentials),
                locales,
                locales.find { it.language == type.preferredLocale }!!,
                type.supportedCurrencies.map { currencyMapper.fromEntity(it) },
                type.supportedDistanceUnits.map { DistanceUnit.parse(it) },
                cardGatewaysMapper.fromEntity(type.cardGateways),
                type.officePhone,
                type.baseUrl)
    }

    override fun toEntity(type: Configs): ConfigsEntity { throw UnsupportedOperationException() }
}
