package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

const val CONFIGS = "configs"

class ApiConfigs(@SerializedName("transport_types") @Expose var transportTypes: ApiTransportTypesWrapper,
                 @SerializedName("paypal_credentials") @Expose var paypalCredentials: ApiPaypalCredentials,
                 @SerializedName("available_locales") @Expose var availableLocales: List<ApiLocales>,
                 @SerializedName("preferred_locale") @Expose var preferredLocale: String,
                 @SerializedName("supported_currencies") @Expose var supportedCurrencies: List<ApiCurrency>,
                 @SerializedName("supported_distance_units") @Expose var supportedDistanceUnits: List<String>,
                 @SerializedName("card_gateways") @Expose var cardGateways: ApiCardGateways,
                 @SerializedName("office_phone") @Expose var officePhone: String,
                 @SerializedName("base_url") @Expose var baseUrl: String) 

/**
 * Wrapper is used to intercept API `transport_types` and convert it into List<ApiTransportType> with origin order.
 */
class ApiTransportTypesWrapper: ArrayList<ApiTransportType>()

class ApiTransportType(@SerializedName("id") @Expose val id: String,
                       @SerializedName("pax_max") @Expose val paxMax: Int,
                       @SerializedName("luggage_max") @Expose val luggageMax: Int)

class ApiPaypalCredentials(@SerializedName("id") @Expose val id: String,
                           @SerializedName("env") @Expose val env: String)

class ApiLocales(@SerializedName("code") @Expose val code: String,
                 @SerializedName("title") @Expose val title: String)

class ApiCurrency(@SerializedName("iso_code") @Expose val code: String,
                  @SerializedName("symbol") @Expose val symbol: String)

class ApiCardGateways(@SerializedName("default") @Expose val default: String,
                      @SerializedName("iso_country_code") @Expose var countryCode: String?)
