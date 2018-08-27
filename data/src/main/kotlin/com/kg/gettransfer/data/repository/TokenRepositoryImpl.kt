package com.kg.gettransfer.data.repository

import android.content.SharedPreferences

import com.kg.gettransfer.data.Api
import com.kg.gettransfer.data.model.ApiToken
import com.kg.gettransfer.data.model.ApiResponse

import retrofit2.HttpException

import timber.log.Timber

class TokenRepositoryImpl(private val api: Api, private val apiKey: String, private val prefs: SharedPreferences) {
    private var accessToken = prefs.getString(ACCESS_TOKEN, null)
        
    companion object {
	    val PREFS_CONFIGS = "configs"
	    val ACCESS_TOKEN = "access_token"
    }
    
    /* @TODO: Обрабатывать {"result":"error","error":{"type":"wrong_api_key","details":"API key \"ccd9a245018bfe4f386f4045ee4a006fsss\" not found"}} */
    suspend fun getAccessToken(): String {
        if(accessToken != null) return accessToken!!
            
		val response: ApiResponse<ApiToken> = try {
			api.accessToken(apiKey).await()
		} catch(httpException: HttpException) {
			throw httpException
		}
		Timber.d("access token updated")
		accessToken = response.data!!.token
		val editor = prefs.edit()
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.commit()
        return accessToken!!
    }

    fun removeAccessToken() {
        accessToken = null
        val editor = prefs.edit()
        editor.remove(ACCESS_TOKEN)
        editor.commit()
    }
}
