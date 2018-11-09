package com.kg.gettransfer.domain.model

import java.util.Currency
import java.util.Locale

data class Configs(val transportTypes: List<TransportType>,
                   val paypalCredentials: PaypalCredentials,
                   val availableLocales: List<Locale>,
                   val preferredLocale: Locale,
                   val supportedCurrencies: List<Currency>,
                   val supportedDistanceUnits: List<DistanceUnit>,
                   val cardGateways: CardGateways,
                   val officePhone: String,
                   val baseUrl: String) {
    companion object {
        val DEFAULT = Configs(emptyList<TransportType>(),
                              PaypalCredentials("", ""),
                              emptyList<Locale>(),
                              Locale.getDefault(),
                              emptyList<Currency>(),
                              emptyList<DistanceUnit>(),
                              CardGateways(""),
                              "",
                              "")
    }
}

data class PaypalCredentials(val id: String, val env: String)
data class CardGateways(val default: String, var countryCode: String? = null)
