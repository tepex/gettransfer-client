package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.ConfigsEntity

data class ConfigsModel(
    @SerializedName(ConfigsEntity.TRANSPORT_TYPES) @Expose val transportTypes: TransportTypesWrapperModel,
    @SerializedName(ConfigsEntity.AVAILABLE_LOCALES) @Expose val availableLocales: List<LocaleModel>,
    @SerializedName(ConfigsEntity.PAYMENT_COMMISSION) @Expose val paymentCommission: Float,
    @SerializedName(ConfigsEntity.SUPPORTED_CURRENCIES) @Expose val supportedCurrencies: List<CurrencyModel>,
    @SerializedName(ConfigsEntity.SUPPORTED_DISTANCE_UNITS) @Expose val supportedDistanceUnits: List<String>
)
/**
 * Wrapper is used to intercept API `transport_types` and convert it into List<TransportTypeModel> with origin order.
 */
class TransportTypesWrapperModel : ArrayList<TransportTypeModel>()

fun ConfigsModel.map() =
    ConfigsEntity(
        transportTypes.map { it.map() },
        availableLocales.map { it.map() },
        paymentCommission,
        supportedCurrencies.map { it.map() },
        supportedDistanceUnits
    )
