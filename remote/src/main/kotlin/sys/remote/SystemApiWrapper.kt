package com.kg.gettransfer.sys.remote

import com.google.gson.GsonBuilder
import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.remote.Api

import com.kg.gettransfer.remote.PrivateHttpLoggingInterceptor
import com.kg.gettransfer.remote.TransportTypesDeserializer
import com.kg.gettransfer.remote.model.TransportTypesWrapperModel

import okhttp3.CookieJar
import okhttp3.OkHttpClient

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import org.slf4j.Logger

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class SystemApiWrapper : KoinComponent {

    val log: Logger by inject { parametersOf("GTR-remote-system") }

    private val preferences = get<PreferencesCache>()

    internal lateinit var api: SystemApi
        private set

    internal val gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(TransportTypesWrapperModel::class.java, TransportTypesDeserializer())
        .registerTypeAdapter(ContactEmailsWrapperModel::class.java, ContactEmailsDeserializer())
        .create()

    private var okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(PrivateHttpLoggingInterceptor())
        addInterceptor { chain ->
            val request = chain.request()
            val urlBuilder = request.url().newBuilder()

            val url = urlBuilder.build()
            val builder = request.newBuilder().url(url)

            val isConfigsRequest = request.url().encodedPath() == SystemApi.API_CONFIGS
            if (isConfigsRequest) {
                builder.addHeader(Api.HEADER_TOKEN, preferences.accessToken)
            }

            try {
                chain.proceed(builder.build())
            } catch (e: Exception) {
                log.error("Maybe DNS Exception", e)
                throw IOException(e)
            }
        }
        cookieJar(CookieJar.NO_COOKIES)
    }.build()

    fun changeEndpoint(endpoint: String) {
        api = Retrofit.Builder().apply {
            baseUrl(endpoint)
            callFactory { okHttpClient.newCall(it) }
            addConverterFactory(GsonConverterFactory.create(gson))
        }.build().create(SystemApi::class.java)
    }

    fun checkApiInitialization() {
        if (!::api.isInitialized) {
            changeEndpoint(preferences.endpointUrl)
        }
    }
}
