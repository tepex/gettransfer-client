package com.kg.gettransfer.network


import com.kg.gettransfer.modules.session.SessionModule
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
    @GET("access_token")
    fun getAccessToken(
            @Header("authorization") a: String,
            @Query("api_key") apiKey: String
    ): Call<Response<AccessToken>>


    @GET("transport_types")
    fun getTransportTypes(
    ): Observable<TransportTypesResponse>


    // --


    @FormUrlEncoded
    @POST("login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String
    ): Observable<BooleanResponse>


    //  --


    @GET("transfers")
    fun getTransfers(
    ): Observable<TransfersResponse>


    @POST("transfers")
    @Headers("Content-Type: application/json")
    fun postTransfer(
            @Body transfer: NewTransferField
    ): Observable<NewTransferCreatedResponse>


    // ------


    companion object {
        private const val TAG = "Api"
        private const val X_ACCESS_TOKEN = "X-ACCESS-TOKEN"

        //        const val API_KEY = "23be10dcf06f280a4c0f8dca95434803"
        const val API_KEY = "ololo"
        //        private const val BASE_URL = "https://demo.gettransfer.com/api/"
        private const val BASE_URL = "https://test.gettransfer.com/api/"


        private val interceptor: Interceptor = Interceptor { chain ->
            if (chain.request().header("authorization") == null) {
                val accessToken = SessionModule.getAccessToken() ?: return@Interceptor null

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


        val api: Api by lazy {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(buildHttpClient())
                    .build()

            retrofit.create(Api::class.java)
        }


        private fun buildHttpClient() =
                OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build()
    }
}