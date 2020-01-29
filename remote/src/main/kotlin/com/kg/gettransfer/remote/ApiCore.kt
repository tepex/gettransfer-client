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
import com.kg.gettransfer.sys.data.map
import com.kg.gettransfer.sys.domain.PreferencesRepository

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
    private val preferencesRepository: PreferencesRepository by inject()

    lateinit var apiUrl: String
    private lateinit var apiKey: String
    private lateinit var ipApiKey: String
    private lateinit var checkoutcomKey: String

    internal lateinit var api: Api
    internal lateinit var ipApi: Api
    internal lateinit var checkoutcomApi: Api

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

            val isAccessTokenRequest = request.url().encodedPath() == Api.API_ACCESS_TOKEN
            val isIpApiRequest = request.url().host() == IP_API_HOST_NAME
            val isCheckoutcomRequest = request.url().host().contains(CHECKOUTCOM_HOST_NAME)

            urlBuilder.apply {
                if (!isIpApiRequest && !isCheckoutcomRequest) {
                    addQueryParameter(PARAM_API_KEY, apiKey)
                    addQueryParameter(PARAM_CURRENCY, sessionRepository.account.currency.code)
                    addQueryParameter(PARAM_LOCALE, sessionRepository.account.locale.language)
                } else if (isIpApiRequest) {
                    addQueryParameter(PARAM_IP_API_KEY, ipApiKey)
                }
            }
            val url = urlBuilder.build()

            val builder = request.newBuilder().url(url)
            if (!isAccessTokenRequest && !isIpApiRequest && !isCheckoutcomRequest) {
                builder.addHeader(Api.HEADER_TOKEN, preferences.accessToken)
            } else if (isCheckoutcomRequest) {
                builder.addHeader(Api.CHECKOUTCOM_HEADER_TOKEN, checkoutcomKey)
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

    fun changeEndpoint(endpoint: EndpointEntity) {
        apiKey = endpoint.key
        apiUrl = endpoint.url
        api = createApi(apiUrl)
    }

    fun changeIpApiKey(key: String) {
        ipApiKey = key
        ipApi = createApi(IP_API_SCHEME + IP_API_HOST_NAME)
    }

    fun initCheckoutcomApi(url: String, key: String) {
        if (!::checkoutcomApi.isInitialized) {
            checkoutcomApi = createApi(url.plus("/"))
        }
        checkoutcomKey = key
    }

    private fun createApi(baseUrl: String) =
        Retrofit.Builder().apply {
            baseUrl(baseUrl)
            callFactory { okHttpClient.newCall(it) }
            addConverterFactory(GsonConverterFactory.create())
        }.build().create(Api::class.java)

    private suspend fun checkApiInitialization() {
        if (!::api.isInitialized) {
            preferencesRepository.getResult().getModel().endpoint?.let { changeEndpoint(it.map()) }
        }
        if (!::ipApi.isInitialized) {
            preferencesRepository.getResult().getModel().ipApiKey?.let { changeIpApiKey(it) }
        }
    }

    /**
     * 1. Try to call [apiCall] first time.
     * 2. If response code is 401 (token expired) — try to call [apiCall] second time.
     */
    @Suppress("ThrowsCount")
    internal suspend fun <R> tryTwice(apiCall: suspend () -> R): R =
        try {
            checkApiInitialization()
            apiCall()
        } catch (e: Exception) {
            if (e is RemoteException) throw e /* second invocation */
            val ae = remoteException(e)
            if (!ae.isInvalidToken()) {
                log.error("apiCall", e)
                throw ae
            }

            try {
                getAccessToken()
            } catch (e1: Exception) { throw remoteException(e1) }
            try {
                apiCall()
            } catch (e2: Exception) { throw remoteException(e2) }
        }

    @Suppress("ThrowsCount")
    internal suspend fun <R> tryTwice(id: Long, apiCall: suspend (Long) -> R): R =
        try {
            checkApiInitialization()
            apiCall(id)
        } catch (e: Exception) {
            if (e is RemoteException) throw e /* second invocation */
            val ae = remoteException(e)
            if (!ae.isInvalidToken()) throw ae

            try {
                getAccessToken()
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

    internal suspend fun authOldAccessToken(authKey: String) =
        try {
            val oldToken = preferences.accessToken
            val updatedToken = if (oldToken.isNotEmpty()) oldToken  else null
            getAccessToken(updatedToken, authKey)
        } catch (e: Exception) {
            throw remoteException(e)
        }

    private suspend fun getAccessToken(token: String? = null, authKey: String? = null) {
        val response: ResponseModel<TokenModel> = api.accessToken(token, authKey)
        @Suppress("UnsafeCallOnNullableType")
        preferences.accessToken = response.data!!.token

        if (authKey == null) loginOldUser()
    }

    private suspend fun loginOldUser() {
        preferences.userPassword?.let { password ->
            val email = preferences.userEmail
            val phone = preferences.userPhone
            if (email != null || phone != null) api.login(email, phone, password)
        }
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
            RemoteException(e.code(), msg ?: errorBody, true, type)
        }
        else -> RemoteException(RemoteException.NOT_HTTP, e.message ?: "", false)
    }

    companion object {
        private const val IP_API_SCHEME = "https://"
        private const val IP_API_HOST_NAME = "ip-service.gtrbox.org"
        private const val PARAM_IP_API_KEY  = "api_key"
        private const val CHECKOUTCOM_HOST_NAME = "checkout.com"

        private val ERROR_PATTERN = Regex("^<h1>(.+)</h1>$")

        const val PARAM_API_KEY  = "api_key"
        private const val PARAM_LOCALE   = "locale"
        private const val PARAM_CURRENCY = "currency"
    }
}
