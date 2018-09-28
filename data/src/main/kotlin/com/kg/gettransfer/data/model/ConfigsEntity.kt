package com.kg.gettransfer.data.model

import java.util.Currency
import java.util.Locale

/**
 * Representation for a [ConfigsEntity] fetched from an external layer data source
 */
data class ConfigsEntity(val transportTypes: List<TransportTypeEntity>,
                         val paypalCredentials: PaypalCredentialsEntity,
                         val availableLocales: List<Locale>,
                         val preferredLocale: Locale,
                         val supportedCurrencies: List<Currency>,
                         val supportedDistanceUnits: List<String>,
                         val cardGateways: CardGatewaysEntity,
                         val officePhone: String,
                         val baseUrl: String)

data class TransportTypeEntity(val id: String, val paxMax: Int, val luggageMax: Int)
data class PaypalCredentialsEntity(val id: String, val env: String)
data class CardGatewaysEntity(val default: String, val countryCode: String?)
