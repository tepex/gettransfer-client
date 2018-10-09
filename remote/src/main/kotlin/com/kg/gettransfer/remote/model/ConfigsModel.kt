package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

const val CONFIGS = "configs"

class ConfigsModel(@SerializedName("transport_types") @Expose var transportTypes: TransportTypesWrapperModel,
                   @SerializedName("paypal_credentials") @Expose var paypalCredentials: PaypalCredentialsModel,
                   @SerializedName("available_locales") @Expose var availableLocales: List<LocaleModel>,
                   @SerializedName("preferred_locale") @Expose var preferredLocale: String,
                   @SerializedName("supported_currencies") @Expose var supportedCurrencies: List<CurrencyModel>,
                   @SerializedName("supported_distance_units") @Expose var supportedDistanceUnits: List<String>,
                   @SerializedName("card_gateways") @Expose var cardGateways: CardGatewaysModel,
                   @SerializedName("office_phone") @Expose var officePhone: String,
                   @SerializedName("base_url") @Expose var baseUrl: String) 

/**
 * Wrapper is used to intercept API `transport_types` and convert it into List<TransportTypeModel> with origin order.
 */
class TransportTypesWrapperModel: ArrayList<TransportTypeModel>()

class TransportTypeModel(@SerializedName("id") @Expose val id: String,
                         @SerializedName("pax_max") @Expose val paxMax: Int,
                         @SerializedName("luggage_max") @Expose val luggageMax: Int)

class PaypalCredentialsModel(@SerializedName("id") @Expose val id: String,
                             @SerializedName("env") @Expose val env: String)

class LocaleModel(@SerializedName("code") @Expose val code: String,
                  @SerializedName("title") @Expose val title: String)

class CurrencyModel(@SerializedName("iso_code") @Expose val code: String,
                    @SerializedName("symbol") @Expose val symbol: String)

class CardGatewaysModel(@SerializedName("default") @Expose val default: String,
                        @SerializedName("iso_country_code") @Expose var countryCode: String?)
