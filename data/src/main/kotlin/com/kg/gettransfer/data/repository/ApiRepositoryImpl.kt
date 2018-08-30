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

import kotlinx.coroutines.experimental.*

import okhttp3.CookieJar
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import timber.log.Timber

class ApiRepositoryImpl(private val context: Context, url: String, private val apiKey: String): ApiRepository {
    private var cacheRepository = CacheRepositoryImpl(context)
    private var api: Api
    private var configs: Configs? = null
    
	/**
	 * @throws ApiException
	 */
    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(loggingInterceptor)
        builder.addInterceptor { chain ->
            var request = chain.request()
            if(request.url().encodedPath() != Api.API_ACCESS_TOKEN) request = request.newBuilder()
	                .addHeader(Api.HEADER_TOKEN, cacheRepository.accessToken)
	                .build()
		    chain.proceed(request)
		}
		
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
	
	/**
	 * @throws ApiException
	 */
	override suspend fun getConfigs(): Configs {
		if(configs != null) return configs!!
	    
		val response: ApiResponse<ApiConfigs> = tryTwice { api.getConfigs() }
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
        
        getAccount(true)
        return configs!!
    }
    
    override suspend fun getAccount(request: Boolean): Account {
        var account = cacheRepository.account
        if(request) {
            val response: ApiResponse<ApiAccountWrapper> = tryTwice { api.getAccount() }
            if(response.data?.account != null) account = mapApiAccount(response.data?.account!!)
            cacheRepository.account = account
        }
        return account
    }

    override suspend fun putAccount(account: Account) {
        cacheRepository.account = account
        tryPutAccount(mapAccount(account))
    }
    
    private suspend fun tryPutAccount(apiAccount: ApiAccount): ApiResponse<ApiAccountWrapper> {
        return try { api.putAccount(apiAccount).await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = ApiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw ApiException(e1) }
            return try { api.putAccount(apiAccount).await() } catch(e2: Exception) { throw ApiException(e2) }
        }
    }

    /* Not used now.
    override suspend fun createAccount(account: Account) {
    }
    */
    
    override suspend fun login(email: String, password: String): Account {
        val response: ApiResponse<ApiAccountWrapper> = try {
            api.login(email, password).await()
        } catch(httpException: HttpException) {
            throw httpException
        }
        val account = mapApiAccount(response.data!!.account)
        //cacheRepository.saveAccount(account)
        return account
    }
    
    override fun logout() {
        cacheRepository.accessToken = CacheRepositoryImpl.INVALID_TOKEN
    }
    
    override suspend fun getRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean): RouteInfo {
        val response: ApiResponse<ApiRouteInfo> = tryGetRouteInfo(points, withPrices, returnWay)
        val apiRouteInfo: ApiRouteInfo = response.data!!
        return RouteInfo(apiRouteInfo.success, apiRouteInfo.distance, apiRouteInfo.duration,
				apiRouteInfo.prices?.map { TransportTypePrice(it.key, it.value.minFloat, it.value.min, it.value.max) },
				apiRouteInfo.watertaxi, apiRouteInfo.routes.get(0).legs.get(0).steps.map { it.polyline.points },
				apiRouteInfo.routes.get(0).overviewPolyline.points)
    }
    
    private suspend fun tryGetRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean):
        ApiResponse<ApiRouteInfo> {
        return try { api.getRouteInfo(points, withPrices, returnWay).await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = ApiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw ApiException(e1) }
            return try { api.getRouteInfo(points, withPrices, returnWay).await() } catch(e2: Exception) { throw ApiException(e2) }
        }
    }

    /**
     * 1. Try to call [apiCall] first time.
     * 2. If response code is 401 (token expired) — try to call [apiCall] second time.
     */
    private suspend fun <R> tryTwice(apiCall: () -> Deferred<R>): R {
        return try { apiCall().await() }
        catch(e: Exception) {
            if(e is ApiException) throw e /* second invocation */
            val ae = ApiException(e)
            if(!ae.isInvalidToken()) throw ae

            try { updateAccessToken() } catch(e1: Exception) { throw ApiException(e1) }
            return try { apiCall().await() } catch(e2: Exception) { throw ApiException(e2) }
        }
    }
    /*
    private suspend fun <T, R> tryTwice(vararg param: T, apiCall: (T) -> Deferred<R>): R {
        return apiCall(param).await()
    }
    */

    private suspend fun updateAccessToken() {
        val response: ApiResponse<ApiToken> = api.accessToken(apiKey).await()
        cacheRepository.accessToken = response.data!!.token
    }

    /**
     * Simple mapper: [ApiAccount] -> [Account]
     */
    private fun mapApiAccount(apiAccount: ApiAccount): Account {
        return Account(apiAccount.email, apiAccount.phone,
                       configs!!.availableLocales.find { it.language == apiAccount.locale }!!,
                       configs!!.supportedCurrencies.find { it.currencyCode == apiAccount.currency }!!,
                       apiAccount.distanceUnit, apiAccount.fullName, apiAccount.groups, apiAccount.termsAccepted)
    }
}
