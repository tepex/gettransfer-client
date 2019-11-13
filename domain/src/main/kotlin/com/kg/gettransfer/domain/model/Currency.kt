package com.kg.gettransfer.domain.model

data class Currency(
    val code: String,
    val symbol: String
) {

    override fun hashCode() = code.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is Currency) return false
        return code == other.code
    }

    companion object {
        val DEFAULT = Currency("USD", "US\$")
        val POPULAR = arrayOf(DEFAULT.code, "EUR", "GBP", "RUB")
        val DEFAULT_LIST = listOf<Currency>(
            Currency("RUB", "\u20BD"),
            Currency("THB", "฿"),
            DEFAULT,
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
            Currency("PHP", "₱")
        )
    }
}
