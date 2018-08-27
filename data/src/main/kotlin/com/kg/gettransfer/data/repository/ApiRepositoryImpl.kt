package com.kg.gettransfer.data.repository

import android.content.Context

import com.kg.gettransfer.data.Api
import com.kg.gettransfer.data.TransportTypesDeserializer

import com.kg.gettransfer.data.model.*
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.ApiRepository

import com.google.gson.GsonBuilder

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory

import java.util.Currency
import java.util.Locale

import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import timber.log.Timber

class ApiRepositoryImpl(private val context: Context, url: String, private val apiKey: String): ApiRepository {
    private var cacheRepository = CacheRepositoryImpl(context)
	private var api: Api
	private var configs: Configs? = null
	
	init {
		val interceptor = HttpLoggingInterceptor()
		interceptor.level = HttpLoggingInterceptor.Level.BODY
		val builder = OkHttpClient.Builder()
		builder.addInterceptor(interceptor)
		builder.cookieJar(CookieJar.NO_COOKIES)
 
		val gson = GsonBuilder()
			.registerTypeAdapter(ApiTransportTypesWrapper::class.java, TransportTypesDeserializer())
			.create()

	    api = Retrofit.Builder()
		        .baseUrl(url)
		        .client(builder.build())
		        .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(CoroutineCallAdapterFactory()) // https://github.com/JakeWharton/retrofit2-kotlin-coroutines-adapter
                .build()
                .create(Api::class.java)
	}
	
	/* @TODO: Обрабатывать {"result":"error","error":{"type":"wrong_api_key","details":"API key \"ccd9a245018bfe4f386f4045ee4a006fsss\" not found"}} */
	private suspend fun getAccessToken(): String {
	    var accessToken = cacheRepository.accessToken
	    if(accessToken != null) return accessToken
	    
	    val response: ApiResponse<ApiToken> = try {
	        api.accessToken(apiKey).await()
	    } catch(httpException: HttpException) {
	        throw httpException
	    }
	    accessToken = response.data!!.token
	    Timber.d("access token updated: $accessToken")
	    cacheRepository.accessToken = accessToken
	    return accessToken
	}
	
	override suspend fun getConfigs(): Configs {
		if(configs != null) return configs!!
			
		val response: ApiResponse<ApiConfigs> = try {
			api.getConfigs(getAccessToken()).await()
		} catch(httpException: HttpException) {
			throw httpException
		}
		val data: ApiConfigs = response.data!!
		
		val locales = data.availableLocales.map { Locale(it.code) }
		configs = Configs(data.transportTypes.map { TransportType(it.id, it.paxMax, it.luggageMax) },
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
	
	override suspend fun getAccount(): Account {
	    var account = cacheRepository.account
		if(account !== Account.EMPTY) return account

		val response: ApiResponse<ApiAccountWrapper> = try {
			api.getAccount(getAccessToken()).await()
		} catch(httpException: HttpException) {
			throw httpException
		}
		if(response.data?.account != null) account = mapApiAccount(response.data?.account!!)
	    cacheRepository.account = account
	    return account
	}
	
	override suspend fun setAccount(account: Account) {
	    cacheRepository.account = account
	}
	
	override suspend fun createAccount(account: Account) {
	}

	override suspend fun login(email: String, password: String): Account {
		val responce: ApiResponse<ApiAccountWrapper> = try {
		    api.login(getAccessToken(), email, password).await()
		} catch(httpException: HttpException) {
			throw httpException
		}
		val account = mapApiAccount(responce.data!!.account)
		//cacheRepository.saveAccount(account)
		return account
	}
	
	override fun logout() {
	    cacheRepository.accessToken = null
	    cacheRepository.account = Account.EMPTY
	}
	
	private fun mapApiAccount(apiAccount: ApiAccount): Account {
		return  Account(apiAccount.email, apiAccount.phone,
				configs!!.availableLocales.find { it.language == apiAccount.locale }!!,
				configs!!.supportedCurrencies.find { it.currencyCode == apiAccount.currency }!!,
				apiAccount.distanceUnit, apiAccount.fullName, apiAccount.groups, apiAccount.termsAccepted)
	}

	override suspend fun getRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean): RouteInfo {
		if (accessToken == null) updateToken()

		val response: ApiResponse<ApiRouteInfo> = try {
		    api.getRouteInfo(accessToken!!, points, withPrices, returnWay).await()
		} catch(httpException: HttpException) {
			throw httpException
		}

		val apiRouteInfo: ApiRouteInfo = response.data!!
		return RouteInfo(apiRouteInfo.success, apiRouteInfo.distance, apiRouteInfo.duration,
				apiRouteInfo.prices?.map { TransportTypePrice(it.key, it.value.minFloat, it.value.min, it.value.max) },
				apiRouteInfo.watertaxi, apiRouteInfo.routes.get(0).legs.get(0).steps.map { it.polyline.points },
				apiRouteInfo.routes.get(0).overviewPolyline.points)
	}
}
