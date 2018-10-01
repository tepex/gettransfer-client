package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.CardGatewaysEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.PaypalCredentialsEntity
import com.kg.gettransfer.data.model.TransportTypeEntity

import com.kg.gettransfer.remote.model.ConfigsModel

import java.util.Currency
import java.util.Locale

/**
 * Map a [ConfigsModel] from a [ConfigsEntity] instance when data is moving between this later and the Data layer.
 */
open class ConfigsMapper(): EntityMapper<ConfigsModel, ConfigsEntity> {
    override fun fromRemote(type: ConfigsModel): ConfigsEntity {
        val locales = type.availableLocales.map { Locale(it.code) }
        return ConfigsEntity(type.transportTypes.map { TransportTypeEntity(it.id, it.paxMax, it.luggageMax) },
                             PaypalCredentialsEntity(type.paypalCredentials.id, type.paypalCredentials.env),
                             locales,
                             locales.find { it.language == type.preferredLocale }!!,
                             type.supportedCurrencies.map { Currency.getInstance(it.code)!! },
                             type.supportedDistanceUnits,
                             CardGatewaysEntity(type.cardGateways.default, type.cardGateways.countryCode),
                             type.officePhone,
                             type.baseUrl)
    }
    
    override fun toRemote(type: ConfigsEntity): ConfigsModel { throw UnsupportedOperationException() }
}
