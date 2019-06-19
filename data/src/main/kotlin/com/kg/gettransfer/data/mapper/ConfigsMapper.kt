package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

import org.koin.standalone.get

/**
 * Map a [ConfigsEntity] to and from a [Configs] instance when data is moving between this later and the Domain layer.
 */
open class ConfigsMapper : Mapper<ConfigsEntity, Configs> {
    private val transportTypeMapper     = get<TransportTypeMapper>()

    /**
     * Map a [ConfigsEntity] instance to a [Configs] instance
     */
    override fun fromEntity(type: ConfigsEntity) =
        Configs(
            transportTypes = type.transportTypes.map { transportTypeMapper.fromEntity(it) },
            //paypalCredentials = paypalCredentialsMapper.fromEntity(type.paypalCredentials),
            availableLocales = type.availableLocales.map { it.map() },
            //preferredLocale = locales.find { it.language == type.preferredLocale },
            //cardGateways = cardGatewaysMapper.fromEntity(type.cardGateways),
            //defaultCardGateways = type.defaultCardGateways,
            paymentCommission = type.paymentCommission,
            supportedCurrencies = type.supportedCurrencies.map { it.map() },
            supportedDistanceUnits = type.supportedDistanceUnits.map { DistanceUnit.valueOf(it) }
            //officePhone = type.officePhone,
            //baseUrl = type.baseUrl
        )

    override fun toEntity(type: Configs): ConfigsEntity {
        throw UnsupportedOperationException()
    }
}
