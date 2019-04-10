package com.kg.gettransfer.domain.model

import java.util.Locale

data class Configs(
    val transportTypes: List<TransportType>,
    //val paypalCredentials: PaypalCredentials,
    val availableLocales: List<Locale>,
    //val preferredLocale: Locale? = null,   //не используется, но может прийти null с сервера
    //val cardGateways: CardGateways,
    //val defaultCardGateways: String,
    val paymentCommission: Float,
    val supportedCurrencies: List<Currency>,
    val supportedDistanceUnits: List<DistanceUnit>
    //val officePhone: String,
    //val baseUrl: String
) {
    companion object {
        private val DEFAULT_CURRENCIES = listOf<Currency>(
                Currency("RUB", "\u20BD"),
                Currency("THB", "฿"),
                Currency("USD", "\$"),
                Currency("GBP", "£"),
                Currency("CNY", "元"),
                Currency("EUR", "€"),
                Currency("AUD", "A\$"),
                Currency("AZN", "\u20BC"),
                Currency("ARS", "\$m/n"),
                Currency("BHD", "ب.د"),
                Currency("BGN", "лв."),
                Currency("BRL", "R\$"),
                Currency("HUF", "Ft"),
                Currency("HKD", "HK\$"),
                Currency("GEL", "ლ"),
                Currency("DKK", "DKK"),
                Currency("AED", "د.إ"),
                Currency("FJD", "FJ\$"),
                Currency("EGP", "ج.م"),
                Currency("ILS", "₪"),
                Currency("INR", "₹"),
                Currency("IDR", "Rp"),
                Currency("JOD", "د.ا"),
                Currency("KZT", "〒"),
                Currency("CAD", "C\$"),
                Currency("QAR", "ر.ق"),
                Currency("COP", "COL\$"),
                Currency("KWD", "د.ك"),
                Currency("MYR", "RM"),
                Currency("MXN", "MEX\$"),
                Currency("MDL", "L"),
                Currency("NAD", "N\$"),
                Currency("NZD", "NZ\$"),
                Currency("RON", "Lei"),
                Currency("NOK", "NOK"),
                Currency("OMR", "ر.ع."),
                Currency("PLN", "zł"),
                Currency("SAR", "ر.س"),
                Currency("SGD", "S\$"),
                Currency("TWD", "NT\$"),
                Currency("TRY", "₺"),
                Currency("UAH", "₴"),
                Currency("XOF", "CFA"),
                Currency("CZK", "Kč"),
                Currency("CLP", "CLP"),
                Currency("SEK", "SEK"),
                Currency("CHF", "CHF"),
                Currency("ZAR", "R"),
                Currency("KRW", "₩"),
                Currency("JPY", "円"),
                Currency("PHP", "₱"))

        private val DEFAULT_LOCALES = listOf(Locale("en"),
                Locale("ru"),
                Locale("de"),
                Locale("fr"),
                Locale("it"),
                Locale("es"),
                Locale("pt"))
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

        val DEFAULT_CONFIGS = Configs(
                transportTypes         = TransportType.DEFAULT_TRANSPORT_TYPES,
                //paypalCredentials      = PaypalCredentials("", ""),
                availableLocales       = DEFAULT_LOCALES,
                //preferredLocale        = Locale.getDefault(),
                //cardGateways           = CardGateways("", null),
                //defaultCardGateways    = "",
                paymentCommission      = 2f,
                supportedCurrencies    = DEFAULT_CURRENCIES,
                supportedDistanceUnits = DistanceUnit.DEFAULT_DISTANCE_UNITS
                //officePhone            = "+74994040505",
                //baseUrl                = "https://gettransfer.com"
        )
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

data class Currency(
    val code: String,
    val symbol: String
) {
    companion object {
        val POPULAR_CURRENCIES = arrayOf("USD", "EUR", "GBP", "RUB")
    }
}
