package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

const val CONFIGS = "configs"

class ConfigsModel(@SerializedName("transport_types") @Expose val transportTypes: TransportTypesWrapperModel,
                   @SerializedName("paypal_credentials") @Expose val paypalCredentials: PaypalCredentialsModel,
                   @SerializedName("available_locales") @Expose val availableLocales: List<LocaleModel>,
                   @SerializedName("preferred_locale") @Expose val preferredLocale: String,
                   @SerializedName("supported_currencies") @Expose val supportedCurrencies: List<CurrencyModel>,
                   @SerializedName("supported_distance_units") @Expose val supportedDistanceUnits: List<String>,
                   @SerializedName("card_gateways") @Expose val cardGateways: CardGatewaysModel,
                   @SerializedName("office_phone") @Expose val officePhone: String,
                   @SerializedName("base_url") @Expose val baseUrl: String) 

/**
 * Wrapper is used to intercept API `transport_types` and convert it into List<TransportTypeModel> with origin order.
 */
class TransportTypesWrapperModel: ArrayList<TransportTypeModel>()

class TransportTypeModel(@SerializedName("id") @Expose val id: String,
                         @SerializedName("pax_max") @Expose val paxMax: Int,
                         @SerializedName("luggage_max") @Expose val luggageMax: Int)

class PaypalCredentialsModel(@SerializedName("id") @Expose val id: String,
                             @SerializedName("env") @Expose val env: String)

class CurrencyModel(@SerializedName("iso_code") @Expose val code: String,
                    @SerializedName("symbol") @Expose val symbol: String)

class CardGatewaysModel(@SerializedName("default") @Expose val default: String,
                        @SerializedName("iso_country_code") @Expose val countryCode: String?)
