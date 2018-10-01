package com.kg.gettransfer.remote

import com.kg.gettransfer.remote.model.*
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.*

interface Api {
    companion object {
        const val HEADER_TOKEN = "X-ACCESS-TOKEN"
        
        const val API_ACCESS_TOKEN = "/api/access_token"
        const val API_CONFIGS      = "/api/configs"
        const val API_ACCOUNT      = "/api/account"
        const val API_LOGIN        = "/api/login"
    }
    
	@GET(API_ACCESS_TOKEN)
	fun accessToken(@Query("api_key") apiKey: String): Deferred<ResponseModel<TokenModel>>
	
	@GET(API_CONFIGS)
	fun getConfigs(): Deferred<ResponseModel<ConfigsModel>>

	@GET(API_ACCOUNT)
	fun getAccount(): Deferred<ResponseModel<AccountWrapperModel>>

	@PUT(API_ACCOUNT)
	fun putAccount(@Body account: AccountModel): Deferred<ResponseModel<AccountWrapperModel>>

	/* If we are not signed in, don't post request to save account
	@POST("/api/account")
	fun postAccount(@Body account: AccountModel): Deferred<ResponseModel<AccountWrapperModel>>
	*/
	
	@POST(API_LOGIN)
	@FormUrlEncoded
	fun login(@Field(ACCOUNT_EMAIL) email: String,
			  @Field(ACCOUNT_PASSWORD) password: String): Deferred<ResponseModel<AccountWrapperModel>>
}
