package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.ConfigsEntity

import com.kg.gettransfer.remote.model.ConfigsModel

import org.koin.standalone.inject

/**
 * Map a [ConfigsModel] from a [ConfigsEntity] instance when data is moving between this later and the Data layer.
 */
open class ConfigsMapper: EntityMapper<ConfigsModel, ConfigsEntity> {
    private val transportTypeMapper: TransportTypeMapper by inject()
    private val paypalCredentialsMapper: PaypalCredentialsMapper by inject()
    private val localeMapper: LocaleMapper by inject()
    private val currencyMapper: CurrencyMapper by inject()
    private val cardGatewaysMapper: CardGatewaysMapper by inject()
    
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
