package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.CardGatewaysEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.PaypalCredentialsEntity
import com.kg.gettransfer.data.model.TransportTypeEntity

import com.kg.gettransfer.domain.model.CardGateways
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.PaypalCredentials
import com.kg.gettransfer.domain.model.TransportType

/**
 * Map a [ConfigsEntity] to and from a [Configs] instance when data is moving between
 * this later and the Domain layer
 */
open class ConfigsMapper(): Mapper<ConfigsEntity, Configs> {
    /**
     * Map a [ConfigsEntity] instance to a [Configs] instance
     */
    override fun fromEntity(type: ConfigsEntity) = 
        Configs(type.transportTypes.map { TransportType(it.id, it.paxMax, it.luggageMax) },
                PaypalCredentials(type.paypalCredentials.id, type.paypalCredentials.env),
                type.availableLocales,
                type.preferredLocale,
                type.supportedCurrencies,
                type.supportedDistanceUnits.map { DistanceUnit.parse(it) },
                CardGateways(type.cardGateways.default, type.cardGateways.countryCode),
                type.officePhone,
                type.baseUrl)

    /**
     * Map a [Configs] instance to a [ConfigsEntity] instance
     */
    override fun toEntity(type: Configs) = 
        ConfigsEntity(type.transportTypes.map { TransportTypeEntity(it.id, it.paxMax, it.luggageMax) },
                      PaypalCredentialsEntity(type.paypalCredentials.id, type.paypalCredentials.env),
                      type.availableLocales,
                      type.preferredLocale,
                      type.supportedCurrencies,
                      type.supportedDistanceUnits.map { it.name },
                      CardGatewaysEntity(type.cardGateways.default, type.cardGateways.countryCode),
                      type.officePhone,
                      type.baseUrl)
}
