package com.kg.gettransfer.remote

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.domain.repository.SessionRepository

import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.TokenModel
import com.kg.gettransfer.remote.model.TransportTypesWrapperModel

import com.kg.gettransfer.sys.data.EndpointEntity

import java.io.IOException

import okhttp3.CookieJar
import okhttp3.OkHttpClient

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

import org.slf4j.Logger

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("TooGenericExceptionCaught")
class ApiCore : KoinComponent {

    val log: Logger by inject { parametersOf("GTR-remote") }
    private val preferences = get<PreferencesCache>()
    private val sessionRepository: SessionRepository by inject()

    internal lateinit var api: Api
    lateinit var apiUrl: String

    private lateinit var apiKey: String
    private lateinit var ipApiKey: String
    private val gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(TransportTypesWrapperModel::class.java, TransportTypesDeserializer())
//        .registerTypeAdapter(ContactEmailsWrapperModel::class.java, ContactEmailsDeserializer())
        .create()

    private var okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(PrivateHttpLoggingInterceptor())
        addInterceptor { chain ->
            val request = chain.request()
            val urlBuilder = request.url().newBuilder()
            urlBuilder.apply {
                if (request.url().host() != IPAPI_HOST_NAME) {
                    addQueryParameter(PARAM_API_KEY, apiKey)
                    addQueryParameter(PARAM_CURRENCY, sessionRepository.account.currency.code)
                    addQueryParameter(PARAM_LOCALE, sessionRepository.account.locale.language)
                } else {
                    addQueryParameter(PARAM_IPAPI_KEY, ipApiKey)
                }
            }
            val url = urlBuilder.build()

            val builder = request.newBuilder().url(url)
            if (url.encodedPath() != Api.API_ACCESS_TOKEN && url.host() != IPAPI_HOST_NAME) {
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
        baseUrl(IPAPI_SCHEME + IPAPI_HOST_NAME)
        callFactory { okHttpClient.newCall(it) }
        addConverterFactory(GsonConverterFactory.create())
    }.build().create(Api::class.java)

    fun changeEndpoint(endpoint: EndpointEntity) {
        apiKey = endpoint.key
        apiUrl = endpoint.url
        api = Retrofit.Builder().apply {
            baseUrl(apiUrl)
            callFactory { okHttpClient.newCall(it) }
            addConverterFactory(GsonConverterFactory.create(gson))
        }.build().create(Api::class.java)
    }

    fun changeIpApiKey(key: String) {
        ipApiKey = key
    }

    /**
     * 1. Try to call [apiCall] first time.
     * 2. If response code is 401 (token expired) — try to call [apiCall] second time.
     */
    @Suppress("ThrowsCount")
    internal suspend fun <R> tryTwice(apiCall: suspend () -> R): R =
        try {
            apiCall()
        } catch (e: Exception) {
            if (e is RemoteException) throw e /* second invocation */
            val ae = remoteException(e)
            if (!ae.isInvalidToken()) {
                log.error("apiCall", e)
                throw ae
            }

            try {
                updateAccessToken()
            } catch (e1: Exception) { throw remoteException(e1) }
            try {
                apiCall()
            } catch (e2: Exception) { throw remoteException(e2) }
        }

    @Suppress("ThrowsCount")
    internal suspend fun <R> tryTwice(id: Long, apiCall: suspend (Long) -> R): R =
        try {
            apiCall(id)
        } catch (e: Exception) {
            if (e is RemoteException) throw e /* second invocation */
            val ae = remoteException(e)
            if (!ae.isInvalidToken()) throw ae

            try {
                updateAccessToken()
            } catch (e1: Exception) { throw remoteException(e1) }
            try {
                apiCall(id)
            } catch (e2: Exception) { throw remoteException(e2) }
        }

    /*
    private suspend fun <T, R> tryTwice(vararg param: T, apiCall: (T) -> R): R {
        return apiCall(param)
    }
    */

    private suspend fun updateAccessToken() {
        val response: ResponseModel<TokenModel> = api.accessToken()
        @Suppress("UnsafeCallOnNullableType")
        preferences.accessToken = response.data!!.token
        val email = preferences.userEmail
        val phone = preferences.userPhone
        val password = preferences.userPassword
        if (email != null || phone != null) api.login(email, phone, password)
    }

    internal fun remoteException(e: Exception): RemoteException = when (e) {
        is HttpException -> {
            val errorBody = e.response()?.errorBody()?.string() ?: error("Exception response is null")
            val msg = try {
                gson.fromJson(errorBody, ResponseModel::class.java).error?.details?.toString()
            } catch (je: JsonSyntaxException) {
                val matchResult = ERROR_PATTERN.find(errorBody)?.groupValues
                log.warn("${e.message} matchResult: $matchResult", je)
                matchResult?.getOrNull(1)
            }
            val type = try {
                gson.fromJson(errorBody, ResponseModel::class.java).error?.type
            } catch (js: JsonSyntaxException) {
                null
            }
            RemoteException(e.code(), msg ?: e.message ?: "", type)
        }
        else -> RemoteException(RemoteException.NOT_HTTP, e.message ?: "")
    }

    companion object {
        private const val IPAPI_SCHEME = "https://"
        private const val IPAPI_HOST_NAME = "ipapi.co"
        private const val PARAM_IPAPI_KEY  = "key"

        private val ERROR_PATTERN = Regex("^<h1>(.+)</h1>$")

        const val PARAM_API_KEY  = "api_key"
        private const val PARAM_LOCALE   = "locale"
        private const val PARAM_CURRENCY = "currency"
    }
}
