package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.ConfigsEntity

import com.kg.gettransfer.remote.model.ConfigsModel

import org.koin.standalone.get

/**
 * Map a [ConfigsModel] from a [ConfigsEntity] instance when data is moving between this later and the Data layer.
 */
open class ConfigsMapper: EntityMapper<ConfigsModel, ConfigsEntity> {
    private val transportTypeMapper     = get<TransportTypeMapper>()
    private val paypalCredentialsMapper = get<PaypalCredentialsMapper>()
    private val localeMapper            = get<LocaleMapper>()
    private val currencyMapper          = get<CurrencyMapper>()
    private val cardGatewaysMapper      = get<CardGatewaysMapper>()

    override fun fromRemote(type: ConfigsModel) =
        ConfigsEntity(type.transportTypes.map { transportTypeMapper.fromRemote(it) },
                      paypalCredentialsMapper.fromRemote(type.paypalCredentials),
                      type.availableLocales.map { localeMapper.fromRemote(it) },
                      type.preferredLocale,
                      type.supportedCurrencies.map { currencyMapper.fromRemote(it) },
                      type.supportedDistanceUnits,
                      cardGatewaysMapper.fromRemote(type.cardGateways),
                      type.officePhone,
                      type.baseUrl)

    override fun toRemote(type: ConfigsEntity): ConfigsModel { throw UnsupportedOperationException() }
}
