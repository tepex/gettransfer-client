package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiConfigs(@SerializedName("transport_types") @Expose var transportTypes: Map<String, ApiTransportType>,
                 @SerializedName("paypal_credentials") @Expose var paypalCredentials: ApiPaypalCredentials,
                 @SerializedName("available_locales") @Expose var availableLocales: List<ApiLocales>,
                 @SerializedName("preferred_locale") @Expose var preferredLocale: String,
                 @SerializedName("supported_currencies") @Expose var supportedCurrencies: List<ApiCurrency>)
	/*
	@SerializedName("supported_distance_units")
	@Expose
	var distanceUnits: List<String>
	
	@SerializedName("card_gateways")
	@Expose
	var cardGateway: ApiCardGateway
	
	@SerializedName("office_phone")
	@Expose
	var officePhone: String 

	@SerializedName("base_url")
	@Expose
	var baseUrl: String 
	*/

class ApiTransportType(@SerializedName("id") @Expose val id: String,
                       @SerializedName("pax_max") @Expose val paxMax: Int,
                       @SerializedName("luggage_max") @Expose val luggageMax: Int)

class ApiPaypalCredentials(@SerializedName("id") @Expose val id: String,
                           @SerializedName("env") @Expose val env: String)

class ApiLocales(@SerializedName("code") @Expose val code: String,
                 @SerializedName("title") @Expose val title: String)

class ApiCurrency(@SerializedName("iso_code") @Expose val code: String,
                  @SerializedName("symbol") @Expose val symbol: String)
/*
	@SerializedName("")
	@Expose
	var : 
*/
/*
{"result":"success",
"data":{
	"card_gateways":{"default":"platron"},
	"supported_distance_units":["km","mi"],
	"office_phone":"+7 499 404 05 05",
	"base_url":"http://stgtr.org"
}}

		
		
		*/
		
		
		