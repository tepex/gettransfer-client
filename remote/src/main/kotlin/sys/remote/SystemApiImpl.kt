package com.kg.gettransfer.sys.remote

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.remote.PrivateHttpLoggingInterceptor

import com.kg.gettransfer.remote.model.EndpointModel

import java.io.IOException

import okhttp3.CookieJar
import okhttp3.OkHttpClient

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

import org.slf4j.Logger

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("TooGenericExceptionCaught")
class SystemApiImpl : KoinComponent {

    private val preferences = get<PreferencesCache>()
    private val log: Logger by inject { parametersOf("GTR-remote") }

    internal lateinit var api: SystemApi
    lateinit var apiUrl: String

    private val gson = GsonBuilder().setLenient().create()

    private var okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(PrivateHttpLoggingInterceptor())
        addInterceptor { chain ->
            val request = chain.request()
            val urlBuilder = request.url().newBuilder()
            val url = urlBuilder.build()

            val builder = request.newBuilder().url(url)
            try {
                chain.proceed(builder.build())
            } catch (e: Exception) {
                log.error("Maybe DNS Exception: ${e.message}")
                throw RemoteException(RemoteException.NOT_HTTP, e.message ?: "")
            }
        }
        .cookieJar(CookieJar.NO_COOKIES)
    }.build()

    fun changeEndpoint(endpoint: EndpointModel) {
        apiUrl = endpoint.url
        api = Retrofit.Builder().apply {
            baseUrl(apiUrl)
            client(okHttpClient)
            addConverterFactory(GsonConverterFactory.create(gson))
            // https://github.com/JakeWharton/retrofit2-kotlin-coroutines-adapter
            addCallAdapterFactory(CoroutineCallAdapterFactory())
        }.build().create(SystemApi::class.java)
    }
}
