package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.ConfigsEntity

import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

import org.koin.standalone.get

/**
 * Map a [ConfigsEntity] to and from a [Configs] instance when data is moving between this later and the Domain layer.
 */
open class ConfigsMapper: Mapper<ConfigsEntity, Configs> {
    private val transportTypeMapper = get<TransportTypeMapper>()
    private val paypalCredentialsMapper = get<PaypalCredentialsMapper>()
    private val localeMapper = get<LocaleMapper>()
    private val currencyMapper = get<CurrencyMapper>()
    private val cardGatewaysMapper = get<CardGatewaysMapper>()

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
