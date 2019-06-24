package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.DistanceUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representation for a [ConfigsEntity] fetched from an external layer data source
 */
@Serializable
data class ConfigsEntity(
    @SerialName(TRANSPORT_TYPES) val transportTypes: List<TransportTypeEntity>,
    // @SerialName(PAYPAL_CREDENTIALS) val paypalCredentials: PaypalCredentialsEntity,
    @SerialName(AVAILABLE_LOCALES) val availableLocales: List<LocaleEntity>,
    // @SerialName(PREFERRED_LOCALE) val preferredLocale: String,
    // @SerialName(CARD_GATEWAYS) val cardGateways: CardGatewaysEntity,
    // @SerialName(DEFAULT_CARD_GATEWAYS) val defaultCardGateways: String,
    @SerialName(PAYMENT_COMMISSION) val paymentCommission: Float,
    @SerialName(SUPPORTED_CURRENCIES) val supportedCurrencies: List<CurrencyEntity>,
    @SerialName(SUPPORTED_DISTANCE_UNITS) val supportedDistanceUnits: List<String>
    // @SerialName(OFFICE_PHONE) val officePhone: String,
    // @SerialName(BASE_URL) val baseUrl: String
) {

    companion object {
        const val ENTITY_NAME              = "configs"
        const val TRANSPORT_TYPES          = "transport_types"
        // const val PAYPAL_CREDENTIALS       = "paypal_credentials"
        const val AVAILABLE_LOCALES        = "available_locales"
        // const val PREFERRED_LOCALE         = "preferred_locale"
        // const val CARD_GATEWAYS            = "card_gateways"
        // const val DEFAULT_CARD_GATEWAYS    = "default_card_gateways"
        const val PAYMENT_COMMISSION       = "payment_commission"
        const val SUPPORTED_CURRENCIES     = "supported_currencies"
        const val SUPPORTED_DISTANCE_UNITS = "supported_distance_units"
        // const val OFFICE_PHONE             = "office_phone"
        // const val BASE_URL                 = "base_url"
    }
}

@Serializable
data class PaypalCredentialsEntity(
    @SerialName(ID)  val id: String,
    @SerialName(ENV) val env: String
) {

    companion object {
        const val ID  = "id"
        const val ENV = "env"
    }
}

@Serializable
data class CardGatewaysEntity(
    @SerialName(DEFAULT) val def: String,
    @SerialName(ISO_COUNTRY_CODE) val countryCode: String?
) {

    companion object {
        const val DEFAULT          = "default"
        const val ISO_COUNTRY_CODE = "iso_country_code"
    }
}

@Serializable
data class CurrencyEntity(
    @SerialName(ISO_CODE) val code: String,
    @SerialName(SYMBOL) val symbol: String
) {

    companion object {
        const val ISO_CODE = "iso_code"
        const val SYMBOL   = "symbol"
    }
}

fun CurrencyEntity.map() = Currency(code, symbol)

fun ConfigsEntity.map() =
    Configs(
        transportTypes.map { it.map() },
        // paypalCredentials = paypalCredentialsMapper.fromEntity(type.paypalCredentials),
        availableLocales.map { it.map() },
        // preferredLocale = locales.find { it.language == type.preferredLocale },
        // cardGateways = cardGatewaysMapper.fromEntity(type.cardGateways),
        // defaultCardGateways = type.defaultCardGateways,
        paymentCommission,
        supportedCurrencies.map { it.map() },
        supportedDistanceUnits.map { DistanceUnit.valueOf(it.toUpperCase()) }
        // officePhone = type.officePhone,
        // baseUrl = type.baseUrl
    )
