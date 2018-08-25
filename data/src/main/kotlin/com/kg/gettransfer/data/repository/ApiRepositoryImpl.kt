package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.Api

import com.kg.gettransfer.data.model.*
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.ApiRepository

import java.util.Currency
import java.util.Locale

import retrofit2.HttpException

import timber.log.Timber

class ApiRepositoryImpl(private val api: Api, private val apiKey: String): ApiRepository {
	
	private var accessToken: String? = null
	private var configs: Configs? = null
	
	/* @TODO: Обрабатывать {"result":"error","error":{"type":"wrong_api_key","details":"API key \"ccd9a245018bfe4f386f4045ee4a006fsss\" not found"}} */
	suspend fun updateToken(): String {
		val response: ApiResponse<ApiToken> = try {
			api.accessToken(apiKey).await()
		} catch(httpException: HttpException) {
			throw httpException
		}
		accessToken = response.data?.token
		Timber.d("access token updated: $accessToken")
		return accessToken!!
	}
	
	override suspend fun getConfigs(): Configs {
		if(configs != null) return configs!!
			
		if(accessToken == null) updateToken()
		val response: ApiResponse<ApiConfigs> = try {
			api.getConfigs(accessToken!!).await()
		} catch(httpException: HttpException) {
			throw httpException
		}
		val data: ApiConfigs = response.data!!
		
		val locales = data.availableLocales.map { Locale.forLanguageTag(it.code)!! }
		configs = Configs(data.transportTypes.mapValues { 
		        TransportType(it.value.id, it.value.paxMax, it.value.luggageMax) },
                PaypalCredentials(data.paypalCredentials.id, data.paypalCredentials.env),
                locales,
                locales.find { it.language == data.preferredLocale }!!,
                data.supportedCurrencies.map { Currency.getInstance(it.code)!! },
                data.supportedDistanceUnits,
                CardGateways(data.cardGateways.default, data.cardGateways.countryCode),
                data.officePhone,
                data.baseUrl)
        return configs!!
	}
	
	override suspend fun getAccount(): Account? {
		if(accessToken == null) updateToken()
		if(configs == null) getConfigs()
		
		val response: ApiResponse<ApiAccountWrapper> = try {
			api.getAccount(accessToken!!).await()
		} catch(httpException: HttpException) {
			throw httpException
		}
		if(response.data?.account == null) return null

		val account: ApiAccount = response.data?.account!!
		return Account(account.email, account.phone,
		               configs!!.availableLocales.find { it.language == account.locale }!!,
                       configs!!.supportedCurrencies.find { it.currencyCode == account.currency }!!,
                       account.distanceUnit, account.fullName, account.groups, account.termsAccepted)
	}
}
