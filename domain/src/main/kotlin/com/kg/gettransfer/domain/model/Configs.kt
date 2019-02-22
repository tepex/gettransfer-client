package com.kg.gettransfer.domain.model

import java.util.Currency
import java.util.Locale

data class Configs(
    val transportTypes: List<TransportType>,
    val paypalCredentials: PaypalCredentials,
    val availableLocales: List<Locale>,
    val preferredLocale: Locale? = null,   //не используется, но может прийти null с сервера
    val supportedCurrencies: List<Currency>,
    val supportedDistanceUnits: List<DistanceUnit>,
    val cardGateways: CardGateways,
    val officePhone: String,
    val baseUrl: String
) {
    companion object {
//        val DEFAULT_CONFIGS = Configs(
//                transportTypes         = TransportType.DEFAULT_TRANSPORT_TYPES,
//                paypalCredentials      = PaypalCredentials("", ""),
//                availableLocales       = Configs.DEFAULT_LOCALES,
//                preferredLocale        = Locale.getDefault(),
//                supportedCurrencies    = Configs.DEFAULT_CURRENCIES,
//                supportedDistanceUnits = DistanceUnit.DEFAULT_DISTANCE_UNITS,
//                cardGateways           = CardGateways("", null),
//                officePhone            = "+74994040505",
//                baseUrl                = "https://gettransfer.com"
//        )

//        private val DEFAULT_LOCALES = listOf(Locale("en"),
//                Locale("ru"),
//                Locale("de"),
//                Locale("fr"),
//                Locale("it"),
//                Locale("es"),
//                Locale("pt"),
//                Locale("zh"),
//                Locale("ar"),
//                Locale("tr"),
//                Locale("pl"),
//                Locale("hu"),
//                Locale("cs"),
//                Locale("el"),
//                Locale("sk"),
//                Locale("he"),
//                Locale("sv"),
//                Locale("fi"),
//                Locale("no"),
//                Locale("ja"),
//                Locale("nl"))
//        private val DEFAULT_CURRENCIES = listOf(Currency.getInstance("RUB"),
//                Currency.getInstance("THB"),
//                Currency.getInstance("USD"),
//                Currency.getInstance("GBP"),
//                Currency.getInstance("CNY"),
//                Currency.getInstance("EUR"),
//                Currency.getInstance("AUD"),
//                Currency.getInstance("AZN"),
//                Currency.getInstance("ARS"),
//                Currency.getInstance("BHD"),
//                Currency.getInstance("BGN"),
//                Currency.getInstance("BRL"),
//                Currency.getInstance("HUF"),
//                Currency.getInstance("HKD"),
//                Currency.getInstance("GEL"),
//                Currency.getInstance("DKK"),
//                Currency.getInstance("AED"),
//                Currency.getInstance("FJD"),
//                Currency.getInstance("EGP"),
//                Currency.getInstance("ILS"),
//                Currency.getInstance("INR"),
//                Currency.getInstance("IDR"),
//                Currency.getInstance("JOD"),
//                Currency.getInstance("KZT"),
//                Currency.getInstance("CAD"),
//                Currency.getInstance("QAR"),
//                Currency.getInstance("COP"),
//                Currency.getInstance("KWD"),
//                Currency.getInstance("MYR"),
//                Currency.getInstance("MXN"),
//                Currency.getInstance("MDL"),
//                Currency.getInstance("NAD"),
//                Currency.getInstance("NZD"),
//                Currency.getInstance("RON"),
//                Currency.getInstance("NOK"),
//                Currency.getInstance("OMR"),
//                Currency.getInstance("PLN"),
//                Currency.getInstance("SAR"),
//                Currency.getInstance("SGD"),
//                Currency.getInstance("TWD"),
//                Currency.getInstance("TRY"),
//                Currency.getInstance("UAH"),
//                Currency.getInstance("XOF"),
//                Currency.getInstance("CZK"),
//                Currency.getInstance("CLP"),
//                Currency.getInstance("SEK"),
//                Currency.getInstance("CHF"),
//                Currency.getInstance("ZAR"),
//                Currency.getInstance("KRW"),
//                Currency.getInstance("JPY"))
    }
}

data class PaypalCredentials(
    val id: String,
    val env: String
)

data class CardGateways(
    val def: String,
    val countryCode: String?
)
