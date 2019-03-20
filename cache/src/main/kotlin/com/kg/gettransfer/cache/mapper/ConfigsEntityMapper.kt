package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.ConfigsCached
import com.kg.gettransfer.cache.model.CurrencyCachedList
import com.kg.gettransfer.cache.model.LocaleCachedList
import com.kg.gettransfer.cache.model.StringList
import com.kg.gettransfer.cache.model.TransportTypesCachedList

import com.kg.gettransfer.data.model.ConfigsEntity

import org.koin.standalone.get

/**
 * Map a [ConfigsCached] from/to a [ConfigsEntity] instance when data is moving between this later and the Data layer.
 */
open class ConfigsEntityMapper : EntityMapper<ConfigsCached, ConfigsEntity> {
    private val transportTypeMapper     = get<TransportTypeEntityMapper>()
    private val paypalCredentialsMapper = get<PaypalCredentialsEntityMapper>()
    private val localeMapper            = get<LocaleEntityMapper>()
    private val currencyMapper          = get<CurrencyEntityMapper>()
    private val cardGatewaysMapper      = get<CardGatewaysEntityMapper>()

    override fun fromCached(type: ConfigsCached) =
        ConfigsEntity(
            transportTypes = type.transportTypes.list.map { transportTypeMapper.fromCached(it) },
            //paypalCredentials = paypalCredentialsMapper.fromCached(type.paypalCredentials),
            availableLocales = type.availableLocales.list.map { localeMapper.fromCached(it) },
            //preferredLocale = type.preferredLocale,
            //cardGateways = cardGatewaysMapper.fromCached(type.cardGateways),
            //defaultCardGateways = type.defaultCardGateways,
            paymentCommission = type.paymentCommission,
            supportedCurrencies = type.supportedCurrencies.list.map { currencyMapper.fromCached(it) },
            supportedDistanceUnits = type.supportedDistanceUnits.list
            //officePhone = type.officePhone,
            //baseUrl = type.baseUrl
        )

    override fun toCached(type: ConfigsEntity) =
        ConfigsCached(
            transportTypes = TransportTypesCachedList(type.transportTypes.map { transportTypeMapper.toCached(it) }),
            //paypalCredentials = paypalCredentialsMapper.toCached(type.paypalCredentials),
            availableLocales = LocaleCachedList(type.availableLocales.map { localeMapper.toCached(it) }),
            //preferredLocale = type.preferredLocale,
            //cardGateways = cardGatewaysMapper.toCached(type.cardGateways),
            //defaultCardGateways = type.defaultCardGateways,
            paymentCommission = type.paymentCommission,
            supportedCurrencies = CurrencyCachedList(type.supportedCurrencies.map { currencyMapper.toCached(it) }),
            supportedDistanceUnits = StringList(type.supportedDistanceUnits)
            //officePhone = type.officePhone,
            //baseUrl = type.baseUrl
        )
}
