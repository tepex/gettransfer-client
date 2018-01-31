package com.kg.gettransfer.network


import android.util.Log
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


/**
 * Created by denisvakulenko on 25/01/2018.
 */


interface Api {


    @GET("transport_types")
    fun getTransportTypes(
    ): Observable<TransportTypesResponse>


    @GET("access_token")
    fun getAccessToken(
            @Header("authorization") a: String,
            @Query("api_key") apiKey: String
    ): Call<Response<AccessToken>>


    @GET("/api/transfers")
    fun getTransfers(
    ): Observable<TransfersResponse>


    @Headers("Content-Type: application/json")
    @POST("/api/transfers")
    fun postTransfer(
            @Body transfer: TransferFieldPOJO
    ): Observable<NewTransferCreatedResponse>


    // --


    companion object {
        private const val TAG = "Api"
        private const val X_ACCESS_TOKEN = "X-ACCESS-TOKEN"

        private const val API_KEY = "23be10dcf06f280a4c0f8dca95434803"
        //        private const val API_KEY = "ololo"
        private const val BASE_URL = "https://demo.gettransfer.com/api/"
//        private const val BASE_URL = "https://test.gettransfer.com/api/"

        private val interceptor: Interceptor = Interceptor { chain ->
            if (chain.request().header("authorization") == null) {
                if (accessToken == null) {
                    synchronized(api) {
                        if (accessToken == null) {
                            updateAccessToken()
                        }
                        if (accessToken == null) return@Interceptor null
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
                    .baseUrl(BASE_URL)
                    .client(getHttpClient())
                    .build()

            retrofit.create(Api::class.java)
        }


        private fun getHttpClient() =
                OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build()


        private fun updateAccessToken() { // Blocks current thread
            Log.d(TAG, "getAccessToken()")

//            val tasks = LinkedBlockingQueue<Runnable>()

            val response = api.getAccessToken("YES", Api.API_KEY).execute()
            if (response.isSuccessful) {
                val data = response.body()?.data
                Log.d(TAG, "getAccessToken() responded success, accessToken = ${data?.accessToken}")
                accessToken = data?.accessToken
            }

//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.from({ runnable -> tasks.add(runnable) }))
//                    .subscribe({ r ->
//                        if (r.success()) {
//                            Log.d(TAG, "getAccessToken() responded success, accessToken = ${r.data!!.accessToken}")
//                            accessToken = r.data?.accessToken
//                        } else {
//                            Log.d(TAG, "getAccessToken() responded fail, result = ${r.result}")
//                        }
//                    }, { error ->
//                        Log.d(TAG, "getAccessToken() fail, ${error.message}")
//                    })
//
//            tasks.take().run()
        }
    }
}