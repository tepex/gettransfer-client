package com.kg.gettransfer.sys.data

import com.kg.gettransfer.data.model.CurrencyEntity
import com.kg.gettransfer.data.model.LocaleEntity
import com.kg.gettransfer.data.model.TransportTypeEntity
import com.kg.gettransfer.data.model.map
import sys.data.CheckoutCredentialsEntity
import sys.data.GooglePayCredentialsEntity
import sys.data.map

import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.sys.domain.Configs

import java.util.Locale

/**
 * Representation for a [ConfigsEntity] fetched from an external layer data source
 */
data class ConfigsEntity(
    val transportTypes: List<TransportTypeEntity>,
    val availableLocales: List<LocaleEntity>,
    /* TODO: change to Double */
    val paymentCommission: Float,
    val supportedCurrencies: List<CurrencyEntity>,
    val supportedDistanceUnits: List<String>,
    val contactEmails: List<ContactEmailEntity>,
    val checkoutCredentials: CheckoutCredentialsEntity,
    val googlePayCredentials: GooglePayCredentialsEntity,
    val defaultCardGateway: String
) {

    companion object {
        const val ENTITY_NAME              = "configs"
        const val TRANSPORT_TYPES          = "transport_types"
        const val AVAILABLE_LOCALES        = "available_locales"
        const val PAYMENT_COMMISSION       = "payment_commission"
        const val SUPPORTED_CURRENCIES     = "supported_currencies"
        const val SUPPORTED_DISTANCE_UNITS = "supported_distance_units"
        const val CONTACT_EMAILS           = "contact_emails"
        const val CHECKOUT_CREDENTIALS     = "checkoutcom_credentials"
        const val GOOGLEPAY_CREDENTIALS    = "googlepay_credentials"
        const val DEFAULT_CARD_GATEWAY     = "default_card_gateway"
    }
}

fun ConfigsEntity.map() =
    Configs(
        transportTypes.map { it.map() },
        availableLocales.map { it.map() },
        paymentCommission,
        supportedCurrencies.map { it.map() },
        supportedDistanceUnits.map { DistanceUnit.valueOf(it.toUpperCase(Locale.US)) },
        contactEmails.map { it.map() },
        checkoutCredentials.map(),
        googlePayCredentials.map(),
        defaultCardGateway
    )
