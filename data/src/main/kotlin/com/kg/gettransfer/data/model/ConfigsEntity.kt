package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representation for a [ConfigsEntity] fetched from an external layer data source
 */
@Serializable
data class ConfigsEntity(
    @SerialName(TRANSPORT_TYPES) val transportTypes: List<TransportTypeEntity>,
    @SerialName(AVAILABLE_LOCALES) val availableLocales: List<LocaleEntity>,
    @SerialName(PAYMENT_COMMISSION) val paymentCommission: Float,
    @SerialName(SUPPORTED_CURRENCIES) val supportedCurrencies: List<CurrencyEntity>,
    @SerialName(SUPPORTED_DISTANCE_UNITS) val supportedDistanceUnits: List<String>
) {

    companion object {
        const val ENTITY_NAME              = "configs"
        const val TRANSPORT_TYPES          = "transport_types"
        const val AVAILABLE_LOCALES        = "available_locales"
        const val PAYMENT_COMMISSION       = "payment_commission"
        const val SUPPORTED_CURRENCIES     = "supported_currencies"
        const val SUPPORTED_DISTANCE_UNITS = "supported_distance_units"
    }
}

fun ConfigsEntity.map() =
    Configs(
        transportTypes.map { it.map() },
        availableLocales.map { it.map() },
        paymentCommission,
        supportedCurrencies.map { it.map() },
        supportedDistanceUnits.map { DistanceUnit.valueOf(it.toUpperCase()) }
    )
