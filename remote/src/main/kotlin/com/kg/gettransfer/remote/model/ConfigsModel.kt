package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.CardGatewaysEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.CurrencyEntity
import com.kg.gettransfer.data.model.PaypalCredentialsEntity

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

data class PaypalCredentialsModel(
    @SerializedName(PaypalCredentialsEntity.ID) @Expose val id: String,
    @SerializedName(PaypalCredentialsEntity.ENV) @Expose val env: String
)

data class CurrencyModel(
    @SerializedName(CurrencyEntity.ISO_CODE) @Expose val code: String,
    @SerializedName(CurrencyEntity.SYMBOL) @Expose val symbol: String
)

data class CardGatewaysModel(
    @SerializedName(CardGatewaysEntity.DEFAULT) @Expose val def: String,
    @SerializedName(CardGatewaysEntity.ISO_COUNTRY_CODE) @Expose val countryCode: String?
)

fun PaypalCredentialsModel.map() = PaypalCredentialsEntity(id, env)

fun CurrencyModel.map() = CurrencyEntity(code, symbol)

fun CardGatewaysModel.map() = CardGatewaysEntity(def, countryCode)

fun ConfigsModel.map() =
    ConfigsEntity(
        transportTypes.map { it.map() },
        availableLocales.map { it.map() },
        paymentCommission,
        supportedCurrencies.map { it.map() },
        supportedDistanceUnits
    )
