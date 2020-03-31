package com.kg.gettransfer.sys.data

import com.kg.gettransfer.data.model.CurrencyEntity
import com.kg.gettransfer.data.model.LocaleEntity
import com.kg.gettransfer.data.model.TransportTypeEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.PaymentRequest

import com.kg.gettransfer.sys.domain.Configs

import java.util.Locale

import sys.data.CheckoutcomCredentialsEntity
import sys.data.GooglePayCredentialsEntity
import sys.data.map

/**
 * Representation for a [ConfigsEntity] fetched from an external layer data source
 */
data class ConfigsEntity(
    val transportTypes: List<TransportTypeEntity>,
    val availableLocales: List<LocaleEntity>,
    /* TODO change to Double */
    val paymentCommission: Float,
    val supportedCurrencies: List<CurrencyEntity>,
    val supportedDistanceUnits: List<String>,
    val contactEmails: List<ContactEmailEntity>,
    val checkoutcomCredentials: CheckoutcomCredentialsEntity,
    val googlePayCredentials: GooglePayCredentialsEntity,
    val defaultCardGateway: String,
    val codeExpiration: Int
) {

    companion object {
        const val ENTITY_NAME              = "configs"
        const val TRANSPORT_TYPES          = "transport_types"
        const val AVAILABLE_LOCALES        = "available_locales"
        const val PAYMENT_COMMISSION       = "payment_commission"
        const val SUPPORTED_CURRENCIES     = "supported_currencies"
        const val SUPPORTED_DISTANCE_UNITS = "supported_distance_units"
        const val CONTACT_EMAILS           = "contact_emails"
        const val CHECKOUTCOM_CREDENTIALS  = "checkoutcom_credentials"
        const val GOOGLEPAY_CREDENTIALS    = "googlepay_credentials"
        const val DEFAULT_CARD_GATEWAY     = "default_card_gateway"
        const val CODE_EXPIRATION          = "verification_code_expiration"
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
        checkoutcomCredentials.map(),
        googlePayCredentials.map(),
        defaultCardGateway.mapGateway(),
        codeExpiration
    )

fun String.mapGateway(): PaymentRequest.Gateway = enumValueOf<PaymentRequest.Gateway>(toUpperCase(Locale.US))
