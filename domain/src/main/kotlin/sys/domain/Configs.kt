package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.TransportType
import sys.domain.CheckoutCredentials
import sys.domain.GooglePayCredentials

import java.util.Locale

data class Configs(
    val transportTypes: List<TransportType>,
    val availableLocales: List<Locale>,
    val paymentCommission: Float,
    val supportedCurrencies: List<Currency>,
    val supportedDistanceUnits: List<DistanceUnit>,
    val contactEmails: List<ContactEmail>,
    val checkoutCredentials: CheckoutCredentials,
    val googlePayCredentials: GooglePayCredentials
) {

    companion object {
        // private val currenciesFilterList = arrayOf("RUB", "THB", "USD", "GBP", "CNY", "EUR" )
        val LOCALES_FILTER = arrayOf("en", "ru", "de", "es", "it", "pt", "fr", "zh")
    }
}
