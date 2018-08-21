package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiConfigs {
	@SerializedName("transport_types")
	@Expose
	var transportTypes: ApiTransportTypes

	@SerializedName("paypal_credentials")
	@Expose
	lateinit var paypalCredentials: ApiPaypalCredentials
	
	@SerializedName("available_locales")
	@Expose
	lateinit var locales: List<ApiLocale>
	
	@SerializedName("preferred_locale")
	@Expose
	lateinit var preferredLocale: String
	
	@SerializedName("supported_currencies")
	@Expose
	lateinit var currencies: List<ApiCurrency>
	
	@SerializedName("supported_distance_units")
	@Expose
	lateinit var distanceUnits: List<String>
	
	@SerializedName("card_gateways")
	@Expose
	lateinit var cardGateway: ApiCardGateway
	
	@SerializedName("office_phone")
	@Expose
	lateinit var officePhone: String 

	@SerializedName("base_url")
	@Expose
	lateinit var baseUrl: String 
}

/*
	@SerializedName("")
	@Expose
	lateinit var : 
*/
