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
open class ConfigsEntityMapper: EntityMapper<ConfigsCached, ConfigsEntity> {
    private val transportTypeMapper     = get<TransportTypeEntityMapper>()
    private val paypalCredentialsMapper = get<PaypalCredentialsEntityMapper>()
    private val localeMapper            = get<LocaleEntityMapper>()
    private val currencyMapper          = get<CurrencyEntityMapper>()
    private val cardGatewaysMapper      = get<CardGatewaysEntityMapper>()

    override fun fromCached(type: ConfigsCached) =
        ConfigsEntity(type.transportTypes.list.map { transportTypeMapper.fromCached(it) },
                      paypalCredentialsMapper.fromCached(type.paypalCredentials),
                      type.availableLocales.list.map { localeMapper.fromCached(it) },
                      type.preferredLocale,
                      type.supportedCurrencies.list.map { currencyMapper.fromCached(it) },
                      type.supportedDistanceUnits.list,
                      cardGatewaysMapper.fromCached(type.cardGateways),
                      type.officePhone,
                      type.baseUrl)

    override fun toCached(type: ConfigsEntity) =
        ConfigsCached(TransportTypesCachedList(type.transportTypes.map { transportTypeMapper.toCached(it) }),
                      paypalCredentialsMapper.toCached(type.paypalCredentials),
                      LocaleCachedList(type.availableLocales.map { localeMapper.toCached(it) }),
                      type.preferredLocale,
                      CurrencyCachedList(type.supportedCurrencies.map { currencyMapper.toCached(it) }),
                      StringList(type.supportedDistanceUnits),
                      cardGatewaysMapper.toCached(type.cardGateways),
                      type.officePhone,
                      type.baseUrl)
}
