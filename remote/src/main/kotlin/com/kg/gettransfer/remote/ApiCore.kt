package com.kg.gettransfer.remote

import com.google.gson.GsonBuilder

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.remote.model.EndpointModel
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.TokenModel
import com.kg.gettransfer.remote.model.TransportTypesWrapperModel

import devcsrj.okhttp3.logging.HttpLoggingInterceptor

import kotlinx.coroutines.Deferred

import okhttp3.CookieJar
import okhttp3.OkHttpClient

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiCore(private val preferences: PreferencesCache) {
    internal lateinit var api: Api
    lateinit var apiUrl: String

    private lateinit var apiKey: String
    private val gson = GsonBuilder().registerTypeAdapter(TransportTypesWrapperModel::class.java, TransportTypesDeserializer()).create()
    private var okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor(Log.logger))
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
            if(!ae.isInvalidToken()) throw ae

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
        preferences.accessToken = response.data!!.token
    }
    
    internal fun remoteException(e: Exception): RemoteException {
        if(e is HttpException)
            return RemoteException(e.code(), gson.fromJson(e.response().errorBody()?.string(), ResponseModel::class.java).
                error?.details?.toString() ?: e.message!!)
        else return RemoteException(RemoteException.NOT_HTTP, e.message!!)
    }
}
