package com.kg.gettransfer.domain.model

import java.util.Locale

data class Configs(
    val transportTypes: List<TransportType>,
    val availableLocales: List<Locale>,
    val paymentCommission: Float,
    val supportedCurrencies: List<Currency>,
    val supportedDistanceUnits: List<DistanceUnit>,
    val contactEmails: List<ContactEmail>
) {

    companion object {
        private val DEFAULT_LOCALES = listOf(
            Locale("en"),
            Locale("ru"),
            Locale("de"),
            Locale("fr"),
            Locale("it"),
            Locale("es"),
            Locale("pt"),
            Locale("zh")
        )

        val EMPTY = Configs(
            transportTypes         = TransportType.DEFAULT_LIST,
            availableLocales       = DEFAULT_LOCALES,
            paymentCommission      = 2f,
            supportedCurrencies    = Currency.DEFAULT_LIST,
            supportedDistanceUnits = DistanceUnit.DEFAULT_LIST,
            contactEmails          = ContactEmail.DEFAULT_LIST
        )
    }
}
