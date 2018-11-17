package com.kg.gettransfer.remote

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.remote.model.EndpointModel
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.TokenModel
import com.kg.gettransfer.remote.model.TransportTypesWrapperModel

import devcsrj.okhttp3.logging.HttpLoggingInterceptor

import java.util.concurrent.TimeoutException

import kotlinx.coroutines.Deferred

import okhttp3.CookieJar
import okhttp3.OkHttpClient

import org.koin.core.parameter.parametersOf
import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

import org.slf4j.Logger

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiCore(private val preferences: PreferencesCache): KoinComponent {
    companion object {
        private val ERROR_PATTERN = Regex("^\\<h1\\>(.+)\\<\\/h1\\>$")
    }

    private val log: Logger by inject { parametersOf("GTR-remote") }

    internal lateinit var api: Api
    lateinit var apiUrl: String

    private lateinit var apiKey: String
    private val gson = GsonBuilder().registerTypeAdapter(TransportTypesWrapperModel::class.java, TransportTypesDeserializer()).create()

    private var okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor(log))
        addInterceptor { chain ->
            var request = chain.request()
            if(request.url().encodedPath() != Api.API_ACCESS_TOKEN) request = request.newBuilder()
	            .addHeader(Api.HEADER_TOKEN, preferences.accessToken)
	            .build()
	        chain.proceed(request)
	    }
	    .cookieJar(CookieJar.NO_COOKIES)
    }.build()

    fun changeEndpoint(endpoint: EndpointModel) {
        apiKey = endpoint.key
        apiUrl = endpoint.url
        api = Retrofit.Builder()
                .baseUrl(endpoint.url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(CoroutineCallAdapterFactory()) // https://github.com/JakeWharton/retrofit2-kotlin-coroutines-adapter
                .build()
                .create(Api::class.java)
    }

    /**
     * 1. Try to call [apiCall] first time.
     * 2. If response code is 401 (token expired) — try to call [apiCall] second time.
     */
    internal suspend fun <R> tryTwice(apiCall: () -> Deferred<R>): R {
        return try { apiCall().await() }
        catch(e: Exception) {
            if(e is RemoteException) throw e /* second invocation */
            val ae = remoteException(e)
            if(!ae.isInvalidToken()) {
                log.error("apiCall", e)
                throw ae
            }

            try { updateAccessToken() } catch(e1: Exception) { throw remoteException(e1) }
            return try { apiCall().await() } catch(e2: Exception) { throw remoteException(e2) }
        }
    }
    
    internal suspend fun <R> tryTwice(id: Long, apiCall: (Long) -> Deferred<R>): R {
        return try { apiCall(id).await() }
        catch(e: Exception) {
           if(e is RemoteException) throw e /* second invocation */
           val ae = remoteException(e)
           if(!ae.isInvalidToken()) throw ae

           try { updateAccessToken() } catch(e1: Exception) { throw remoteException(e1) }
           return try { apiCall(id).await() } catch(e2: Exception) { throw remoteException(e2) }
        }
    }
    
    /*
    private suspend fun <T, R> tryTwice(vararg param: T, apiCall: (T) -> Deferred<R>): R {
        return apiCall(param).await()
    }
    */

    internal suspend fun updateAccessToken() {
        val response: ResponseModel<TokenModel> = api.accessToken(apiKey).await()
        val accessToken = response.data!!.token
        preferences.accessToken = accessToken
    }
    
    internal fun remoteException(e: Exception): RemoteException {
        return when(e) {
            is HttpException -> {
                val errorBody = e.response().errorBody()?.string()
                val msg = try {
                    gson.fromJson(errorBody, ResponseModel::class.java).error?.details?.toString()
                } catch(je: JsonSyntaxException) {
                    val matchResult = errorBody?.let { ERROR_PATTERN.find(it)?.let { it.groupValues } }
                    log.warn("${e.message} matchResult: $matchResult", je)
                    matchResult?.getOrNull(1)
                }
                RemoteException(e.code(), msg ?: e.message!!)
            }
            else -> RemoteException(RemoteException.NOT_HTTP, e.message!!)
        }
    }
}
