package com.kg.gettransfer.remote

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.domain.repository.SessionRepository

import com.kg.gettransfer.remote.model.EndpointModel
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.TokenModel
import com.kg.gettransfer.remote.model.TransportTypesWrapperModel

import java.io.IOException

import kotlinx.coroutines.Deferred

import okhttp3.CookieJar
import okhttp3.OkHttpClient

import org.koin.core.parameter.parametersOf

import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

import org.slf4j.Logger

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiCore : KoinComponent {
    private val preferences = get<PreferencesCache>()
    private val log: Logger by inject { parametersOf("GTR-remote") }
    private val sessionRepository: SessionRepository by inject()

    internal lateinit var api: Api
    lateinit var apiUrl: String

    private lateinit var apiKey: String
    private val gson = GsonBuilder()
            .registerTypeAdapter(TransportTypesWrapperModel::class.java, TransportTypesDeserializer())
            .create()

    private var okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(PrivateHttpLoggingInterceptor())
        addInterceptor { chain ->
            val request = chain.request()
            val urlBuilder = request.url().newBuilder()
            if(request.url().host() != IP_API_HOST_NAME) {
                urlBuilder.apply {
                    addQueryParameter(PARAM_API_KEY, apiKey)
                    addQueryParameter(PARAM_CURRENCY, sessionRepository.account.currency.code)
                    addQueryParameter(PARAM_LOCALE, sessionRepository.account.locale.language)
                }
            }
            val url = urlBuilder.build()

            val builder = request.newBuilder().url(url)
            if (url.encodedPath() != Api.API_ACCESS_TOKEN && url.host() != IP_API_HOST_NAME) {
                builder.addHeader(Api.HEADER_TOKEN, preferences.accessToken)
            }
            try {
                chain.proceed(builder.build())
            } catch (e: Exception) {
                log.error("Maybe DNS Exception", e)
                throw IOException(e)
            }
        }
        .cookieJar(CookieJar.NO_COOKIES)
    }.build()

    internal var ipApi = Retrofit.Builder().apply {
        baseUrl(IP_API_SCHEME + IP_API_HOST_NAME)
        client(okHttpClient)
        addConverterFactory(GsonConverterFactory.create())
        addCallAdapterFactory(CoroutineCallAdapterFactory())
    }.build().create(Api::class.java)

    fun changeEndpoint(endpoint: EndpointModel) {
        apiKey = endpoint.key
        apiUrl = endpoint.url
        api = Retrofit.Builder().apply {
            baseUrl(apiUrl)
            client(okHttpClient)
            addConverterFactory(GsonConverterFactory.create(gson))
            addCallAdapterFactory(CoroutineCallAdapterFactory()) // https://github.com/JakeWharton/retrofit2-kotlin-coroutines-adapter
        }.build().create(Api::class.java)
    }

    /**
     * 1. Try to call [apiCall] first time.
     * 2. If response code is 401 (token expired) — try to call [apiCall] second time.
     */
    internal suspend fun <R> tryTwice(apiCall: () -> Deferred<R>): R {
        return try { apiCall().await() }
        catch (e: Exception) {
            if (e is RemoteException) throw e /* second invocation */
            val ae = remoteException(e)
            if (!ae.isInvalidToken()) {
                log.error("apiCall", e)
                throw ae
            }

            try { updateAccessToken() } catch (e1: Exception) { throw remoteException(e1) }
            return try { apiCall().await() } catch (e2: Exception) { throw remoteException(e2) }
        }
    }

    internal suspend fun <R> tryTwice(id: Long, apiCall: (Long) -> Deferred<R>): R {
        return try { apiCall(id).await() }
        catch (e: Exception) {
           if (e is RemoteException) throw e /* second invocation */
           val ae = remoteException(e)
           if (!ae.isInvalidToken()) throw ae

           try { updateAccessToken() } catch (e1: Exception) { throw remoteException(e1) }
           return try { apiCall(id).await() } catch (e2: Exception) { throw remoteException(e2) }
        }
    }

    /*
    private suspend fun <T, R> tryTwice(vararg param: T, apiCall: (T) -> Deferred<R>): R {
        return apiCall(param).await()
    }
    */

    internal suspend fun updateAccessToken() {
        val response: ResponseModel<TokenModel> = api.accessToken().await()
        preferences.accessToken = response.data!!.token
        val email = preferences.userEmail
        val phone = preferences.userPhone
        val password = preferences.userPassword
        if (email != null || phone != null) api.login(email, phone, password).await()
    }

    internal fun remoteException(e: Exception): RemoteException = when(e) {
        is HttpException -> {
            val errorBody = e.response().errorBody()?.string()
            val msg = try {
                gson.fromJson(errorBody, ResponseModel::class.java).error?.details?.toString()
            } catch (je: JsonSyntaxException) {
                val matchResult = errorBody?.let { ERROR_PATTERN.find(it)?.groupValues }
                log.warn("${e.message} matchResult: $matchResult", je)
                matchResult?.getOrNull(1)
            }
            val type = try {
                gson.fromJson(errorBody, ResponseModel::class.java).error?.type
            } catch (js: JsonSyntaxException) {
                null
            }
            RemoteException(e.code(), msg ?: e.message!!, type)
        }
        else -> RemoteException(RemoteException.NOT_HTTP, e.message!!)
    }

    companion object {
        private const val IP_API_SCHEME = "https://"
        private const val IP_API_HOST_NAME = "ipapi.co"

        private val ERROR_PATTERN = Regex("^\\<h1\\>(.+)\\<\\/h1\\>$")

        const val PARAM_API_KEY  = "api_key"
        private const val PARAM_LOCALE   = "locale"
        private const val PARAM_CURRENCY = "currency"
    }
}
