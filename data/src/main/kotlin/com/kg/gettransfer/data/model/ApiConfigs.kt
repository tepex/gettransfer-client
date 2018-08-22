package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiConfigs(@SerializedName("transport_types") @Expose var transportTypes: Map<String, ApiTransportType>,
                 @SerializedName("paypal_credentials") @Expose var paypalCredentials: ApiPaypalCredentials,
                 @SerializedName("available_locales") @Expose var availableLocales: List<ApiLocales>,
                 @SerializedName("preferred_locale") @Expose var preferredLocale: String)
	/*
	@SerializedName("supported_currencies")
	@Expose
	var currencies: List<ApiCurrency>
	
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

/*
	@SerializedName("")
	@Expose
	var : 
*/
/*
{"result":"success",
"data":{
	"available_locales":[
		{"code":"en","title":"English"},
		{"code":"de","title":"Deutsch"},
		{"code":"fr","title":"Français"},
		{"code":"it","title":"Italiano"},
		{"code":"es","title":"Español"},
		{"code":"pt","title":"Português"},
		{"code":"nl","title":"Nederlands"},
		{"code":"ru","title":"Русский"},
		{"code":"zh","title":"中文"},
		{"code":"ar","title":"العَرَبِيَّة"}
	],
	"preferred_locale":"en",
	"card_gateways":{"default":"platron"},
	"supported_currencies":[
		{"iso_code":"RUB","symbol":"₽"},
		{"iso_code":"THB","symbol":"฿"},
		{"iso_code":"USD","symbol":"$"},
		{"iso_code":"GBP","symbol":"£"},
		{"iso_code":"CNY","symbol":"¥"},
		{"iso_code":"EUR","symbol":"€"}
	],
	"supported_distance_units":["km","mi"],
	"office_phone":"+7 499 404 05 05",
	"base_url":"http://stgtr.org"
}}

		
		
		*/
		
		
		