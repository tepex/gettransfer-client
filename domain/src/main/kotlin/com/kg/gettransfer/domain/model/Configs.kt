package com.kg.gettransfer.domain.model

import java.util.Currency
import java.util.Locale

data class Configs(val transportTypes: Map<String, TransportType>,
                   val paypalCredentials: PaypalCredentials,
                   val availableLocales: List<Locale>,
                   val preferredLocale: Locale,
                   val supportedCurrencies: List<Currency>)

data class TransportType(val id: String, val paxMax: Int, val luggageMax: Int)
data class PaypalCredentials(val id: String, val env: String)
