package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable

/**
 * Representation for a [ConfigsEntity] fetched from an external layer data source
 */
@Serializable
data class ConfigsEntity(val transportTypes: List<TransportTypeEntity>,
                         val paypalCredentials: PaypalCredentialsEntity,
                         val availableLocales: List<LocaleEntity>,
                         val preferredLocale: String,
                         val supportedCurrencies: List<CurrencyEntity>,
                         val supportedDistanceUnits: List<String>,
                         val cardGateways: CardGatewaysEntity,
                         val officePhone: String,
                         val baseUrl: String)

@Serializable
data class PaypalCredentialsEntity(val id: String, val env: String)
@Serializable
data class CardGatewaysEntity(val default: String, val countryCode: String?)
@Serializable
data class LocaleEntity(val code: String, val title: String)
@Serializable
data class CurrencyEntity(val code: String, val symbol: String)
