package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.ConfigsEntity

import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

import org.koin.standalone.get

/**
 * Map a [ConfigsEntity] to and from a [Configs] instance when data is moving between this later and the Domain layer.
 */
open class ConfigsMapper : Mapper<ConfigsEntity, Configs> {
    private val transportTypeMapper     = get<TransportTypeMapper>()
    private val paypalCredentialsMapper = get<PaypalCredentialsMapper>()
    private val localeMapper            = get<LocaleMapper>()
    private val currencyMapper          = get<CurrencyMapper>()
    private val cardGatewaysMapper      = get<CardGatewaysMapper>()

    /**
     * Map a [ConfigsEntity] instance to a [Configs] instance
     */
    override fun fromEntity(type: ConfigsEntity): Configs {
        val locales = type.availableLocales.map { localeMapper.fromEntity(it) }
        return Configs(
            transportTypes = type.transportTypes.map { transportTypeMapper.fromEntity(it) },
            paypalCredentials = paypalCredentialsMapper.fromEntity(type.paypalCredentials),
            availableLocales = locales,
            preferredLocale = locales.find { it.language == type.preferredLocale },
            cardGateways = cardGatewaysMapper.fromEntity(type.cardGateways),
            paymentCommission = type.paymentCommission,
            supportedCurrencies = type.supportedCurrencies.map { currencyMapper.fromEntity(it) },
            supportedDistanceUnits = type.supportedDistanceUnits.map { DistanceUnit.valueOf(it) },
            officePhone = type.officePhone,
            baseUrl = type.baseUrl
        )
    }

    override fun toEntity(type: Configs): ConfigsEntity {
        throw UnsupportedOperationException()
    }
}
