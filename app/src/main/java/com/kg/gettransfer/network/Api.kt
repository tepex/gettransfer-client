package com.kg.gettransfer.network


import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue


/**
 * Created by denisvakulenko on 25/01/2018.
 */


interface Api {


    @GET("transport_types")
    fun getTransportTypes(): Observable<TransportTypesResponse>


    @GET("access_token")
    fun getAccessToken(
            @Header("authorization") a: String,
            @Query("api_key") apiKey: String
    ): Observable<Response<AccessToken>>


    @GET("/api/transfers")
    fun getTransfers(): Observable<TransferResponse>


    @Headers("Content-Type: application/json")
    @POST("/api/transfers")
    fun postTransfer(
            @Body transfer: TransferFieldPOJO
    ): Observable<TransferResponse>


    // --


    companion object {
        private const val TAG = "Api"
        private const val API_KEY = "23be10dcf06f280a4c0f8dca95434803"
        private const val X_ACCESS_TOKEN = "X-ACCESS-TOKEN"

        private val interceptor: Interceptor = Interceptor { chain ->
            if (chain.request().header("authorization") == null) {
                if (accessToken == null) {
                    synchronized(api) { // TODO: Why not working?
                        if (accessToken == null) {
                            updateAccessToken()
                        }
                        null
                    }
                }

                val original = chain.request()
                val requestBuilder = original.newBuilder()
                requestBuilder.header(X_ACCESS_TOKEN, accessToken)
                requestBuilder.method(original.method(), original.body())
                val request = requestBuilder.build()

                chain.proceed(request)
            } else {
                chain.proceed(chain.request())
            }
        }

        private var accessToken: String? = null


        val api: Api by lazy {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://demo.gettransfer.com/api/")
                    .client(getHttpClient())
                    .build()

            retrofit.create(Api::class.java)
        }


        private fun getHttpClient() =
                OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build()


        private fun updateAccessToken() { // Blocks the thread on which it was called
            Log.d(TAG, "getAccessToken()")
            val tasks = LinkedBlockingQueue<Runnable>()
            api.getAccessToken("YES", Api.API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.from( { runnable -> tasks.add(runnable) }))
                    .subscribe({ r ->
                        if (r.success()) {
                            Log.d(TAG, "getAccessToken() success, accessToken = ${r.data!!.accessToken}")
                            accessToken = r.data?.accessToken
                        } else {
                            Log.d(TAG, "getAccessToken() failed, result = ${r.result}")
                        }
                    }, { error ->
                        Log.d(TAG, "getAccessToken() failed, ${error.message}")
                    })
            tasks.take().run()
        }
    }
}