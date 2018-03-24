package com.kg.gettransfer.modules.http


import android.accounts.NetworkErrorException
import com.kg.gettransfer.modules.Session
import com.kg.gettransfer.modules.http.json.AccessToken
import com.kg.gettransfer.modules.http.json.BaseResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.standalone.KoinComponent
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.logging.Logger


/**
 * Created by denisvakulenko on 07/02/2018.
 */


class ProvideAccessTokenInterceptor(val session: Session, rx2call: RxJava2CallAdapterFactory, gsonConverterFactory: GsonConverterFactory) : Interceptor, KoinComponent {
    private val log = Logger.getLogger("ProvideAccessTokenInterceptor")

    private val X_ACCESS_TOKEN = "X-ACCESS-TOKEN"


    interface HttpAccessApi {
        @GET("access_token")
        fun getAccessToken(
                @Query("api_key") apiKey: String)
                : Call<BaseResponse<AccessToken>>
    }

    private val httpAccessApi: HttpAccessApi

    init {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(rx2call)
                .addConverterFactory(gsonConverterFactory)
                .baseUrl(BASE_URL)
                .client(OkHttpClient.Builder().build())
                .build()

        httpAccessApi = retrofit.create(HttpAccessApi::class.java)
    }

    override fun intercept(chain: Interceptor.Chain?): okhttp3.Response? {
        if (chain == null) return null

        if (!session.hasToken) newSession()

        val accessToken: String = session.accessToken ?: throw Exception("Access denied")

        val original = chain.request()
        val requestBuilder = original.newBuilder()
        requestBuilder.header(X_ACCESS_TOKEN, accessToken)
        requestBuilder.method(original.method(), original.body())
        val request = requestBuilder.build()

        val response = chain.proceed(request)

        if (response.code() == 403) {
            session.logOut()
        }

        return response
    }

    // Blocks current thread
    private fun newSession() {
        synchronized(this) {
            if (session.hasToken) log.info("getAccessToken()")
            val response = httpAccessApi.getAccessToken(API_KEY).execute()
            if (response.isSuccessful) {
                val newAccessToken = response.body()?.data?.accessToken
                log.info("getAccessToken() responded success, new accessToken = $newAccessToken")
                if (newAccessToken != null) {
                    session.reset(newAccessToken)
                }
            } else {
                throw NetworkErrorException(response.code().toString())
            }
        }
    }
}