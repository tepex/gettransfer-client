package com.kg.gettransfer.data.model

/**
 * Representation for a [ConfigsEntity] fetched from an external layer data source
 */
data class ConfigsEntity(val transportTypes: List<TransportTypeEntity>,
                         val paypalCredentials: PaypalCredentialsEntity,
                         val availableLocales: List<LocaleEntity>,
                         val preferredLocale: String,
                         val supportedCurrencies: List<CurrencyEntity>,
                         val supportedDistanceUnits: List<String>,
                         val cardGateways: CardGatewaysEntity,
                         val officePhone: String,
                         val baseUrl: String)

data class TransportTypeEntity(val id: String, val paxMax: Int, val luggageMax: Int)
data class PaypalCredentialsEntity(val id: String, val env: String)
data class CardGatewaysEntity(val default: String, val countryCode: String?)
data class LocaleEntity(val code: String, val title: String)
data class CurrencyEntity(val code: String, val symbol: String)
