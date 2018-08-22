package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ApiConfigs(@SerializedName("transport_types") @Expose var transportTypes: ApiTransportTypeWrapper)

	/*
	@SerializedName("paypal_credentials")
	@Expose
	var paypalCredentials: ApiPaypalCredentials
	
	@SerializedName("available_locales")
	@Expose
	var locales: List<ApiLocale>
	
	@SerializedName("preferred_locale")
	@Expose
	var preferredLocale: String
	
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

data class ApiTransportTypeWrapper(@SerializedName("economy") @Expose var economy: ApiTransportType?,
                                   @SerializedName("business") @Expose var business: ApiTransportType?,
                                   @SerializedName("premium") @Expose var premium: ApiTransportType?,
                                   @SerializedName("van") @Expose var van: ApiTransportType?,
                                   @SerializedName("minibus") @Expose var minibus: ApiTransportType?,
                                   @SerializedName("bus") @Expose var bus: ApiTransportType?,
                                   @SerializedName("limousine") @Expose var limousine: ApiTransportType?,
                                   @SerializedName("helicopter") @Expose var helicopter: ApiTransportType?)

data class ApiTransportType(@SerializedName("id") var id: String,
                            @SerializedName("pax_max") var paxMax: Int,
                            @SerializedName("luggage_max") var luggageMax: Int)


/*
	@SerializedName("")
	@Expose
	var : 
*/
/*
{"result":"success",
"data":{
	"transport_types":{
		"economy":{"id":"economy","pax_max":3,"luggage_max":3},
		"business":{"id":"business","pax_max":3,"luggage_max":3},
		"premium":{"id":"premium","pax_max":3,"luggage_max":3},
		"van":{"id":"van","pax_max":8,"luggage_max":6},
		"minibus":{"id":"minibus","pax_max":16,"luggage_max":16},
		"bus":{"id":"bus","pax_max":50,"luggage_max":50},
		"limousine":{"id":"limousine","pax_max":20,"luggage_max":10},
		"elicopter":{"id":"helicopter","pax_max":5,"luggage_max":2}
	},
	"paypal_credentials":{"env":"sandbox","id":"AdH0J3MmKITP9kCPQPYfKN6zjtkORkF_bxM_a9poib9Wh73iD6WXRDYKGxSPRAV-EkKOoLsgr-z0S1cu"},
	"available_locales":[
		{"code":"en","title":"English"},
		{"code":"de","title":"Deutsch"},
		{"code":"fr","title":"Français"},
		{"code":"it","title":"Italiano"},
		{"code":"es","title":"Español"},
		{"code":"pt","title":"Português"},{"code":"nl","title":"Nederlands"},{"code":"ru","title":"Русский"},{"code":"zh","title":"中文"},{"code":"ar","title":"العَرَبِيَّة"}]
		,"preferred_locale":"en","card_gateways":{"default":"platron"},"supported_currencies":[{"iso_code":"RUB","symbol":"₽"},{"iso_code":"THB","symbol":"฿"},{"iso_code":"USD","symbol":"$"},{"iso_code":"GBP","symbol":"£"},{"iso_code":"CNY","symbol":"¥"},{"iso_code":"EUR","symbol":"€"}],"supported_distance_units":["km","mi"],"office_phone":"+7 499 404 05 05","base_url":"http://stgtr.org"}}

		
		
{"result":"success","data":{"transport_types":{"economy":{"id":"economy","pax_max":3,"luggage_max":3},"business":{"id":"b
                            usiness","pax_max":3,"luggage_max":3},"premium":{"id":"premium","pax_max":3,"luggage_max":3},"van":{"id":"van","pax_max":
                            8,"luggage_max":6},"minibus":{"id":"minibus","pax_max":16,"luggage_max":16},"bus":{"id":"bus","pax_max":50,"luggage_max":
                            50},"limousine":{"id":"limousine","pax_max":20,"luggage_max":10},"helicopter":{"id":"helicopter","pax_max":5,"luggage_max
                            ":2}},"paypal_credentials":{"env":"sandbox","id":"AdH0J3MmKITP9kCPQPYfKN6zjtkORkF_bxM_a9poib9Wh73iD6WXRDYKGxSPRAV-EkKOoLs
                            gr-z0S1cu"},"available_locales":[{"code":"en","title":"English"},{"code":"de","title":"Deutsch"},{"code":"fr","title":"Fr
                            ançais"},{"code":"it","title":"Italiano"},{"code":"es","title":"Español"},{"code":"pt","title":"Português"},{"code":"nl",
                            "title":"Nederlands"},{"code":"ru","title":"Русский"},{"code":"zh","title":"中文"},{"code":"ar","title":"العَرَبِيَّة"}],"p
                            referred_locale":"en","card_gateways":{"default":"platron"},"supported_currencies":[{"iso_code":"RUB","symbol":"₽"},{"iso
                            _code":"THB","symbol":"฿"},{"iso_code":"USD","symbol":"$"},{"iso_code":"GBP","symbol":"£"},{"iso_code":"CNY","symbol":"¥"
                            },{"iso_code":"EUR","symbol":"€"}],"supported_distance_units":["km","mi"],"office_phone":"+7 499 404 05 05","base_url":"h
                            ttp://stgtr.org"}}		
		
		*/
		