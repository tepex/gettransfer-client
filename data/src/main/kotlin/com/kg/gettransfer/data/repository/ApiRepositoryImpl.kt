package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.Api
import com.kg.gettransfer.data.model.*
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.domain.repository.ApiRepository
import retrofit2.HttpException
import timber.log.Timber
import java.util.*

class ApiRepositoryImpl(private val api: Api, private val apiKey: String): ApiRepository {
	
	private var accessToken: String? = null
	private var configs: Configs? = null
	private var account: Account? = null

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
		configs = Configs(data.transportTypes.mapValues { TransportType(it.value.id,
                                                                            it.value.paxMax,
                                                                            it.value.luggageMax) },
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
		if (account != null) return account

		val response: ApiResponse<ApiAccountWrapper> = try {
			api.getAccount(accessToken!!).await()
		} catch(httpException: HttpException) {
			throw httpException
		}
		if(response.data!!.account == null) return null

		val apiAccount: ApiAccount = response.data!!.account!!
		account = Account(apiAccount.email, apiAccount.phone,
		               configs!!.availableLocales.find { it.language == apiAccount.locale }!!,
                       configs!!.supportedCurrencies.find { it.currencyCode == apiAccount.currency }!!,
                       apiAccount.distanceUnit, apiAccount.fullName, apiAccount.groups, apiAccount.termsAccepted)
		return account
	}

	override suspend fun login(email: String, password: String): Account {
		if (accessToken == null) updateToken()

		val responce: ApiResponse<ApiAccountWrapper> = try {
		    api.login(accessToken!!, email, password).await()
		} catch(httpException: HttpException) {
			throw httpException
		}

		val apiAccount: ApiAccount = responce.data!!.account
		return  Account(apiAccount.email, apiAccount.phone,
				configs!!.availableLocales.find { it.language == apiAccount.locale }!!,
				configs!!.supportedCurrencies.find { it.currencyCode == apiAccount.currency }!!,
				apiAccount.distanceUnit, apiAccount.fullName, apiAccount.groups, apiAccount.termsAccepted)
	}
}