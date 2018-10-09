package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.ConfigsEntity

import com.kg.gettransfer.remote.model.ConfigsModel

/**
 * Map a [ConfigsModel] from a [ConfigsEntity] instance when data is moving between this later and the Data layer.
 */
open class ConfigsMapper(private val transportTypeMapper: TransportTypeMapper,
                         private val paypalCredentialsMapper: PaypalCredentialsMapper,
                         private val localeMapper: LocaleMapper,
                         private val currencyMapper: CurrencyMapper,
                         private val cardGatewaysMapper: CardGatewaysMapper): EntityMapper<ConfigsModel, ConfigsEntity> {
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
