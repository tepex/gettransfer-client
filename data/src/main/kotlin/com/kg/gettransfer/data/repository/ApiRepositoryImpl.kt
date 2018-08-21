package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.Api
import com.kg.gettransfer.data.model.*

import com.kg.gettransfer.domain.repository.ApiRepository

import retrofit2.HttpException

import timber.log.Timber

class ApiRepositoryImpl(private val api: Api, private val apiKey: String): ApiRepository {
	
	private var accessToken: String? = null
	
	override suspend fun updateToken(): String {
		val response: Response<Token> = try {
			api.accessToken(apiKey).await()
		} catch(httpException: HttpException) {
			Timber.e(httpException)
			throw RuntimeException(httpException)
		}
		accessToken = response.data?.token
		return accessToken!!
	}
	/*
	fun <T> request(call: Deferred<T>, callback: NetworkCallback<T>) {
        request(call, callback.success, callback.error)
    }

	fun request(call: Deferred<T>, onSuccess: ((T) -> Unit)?, onError: ((Throwable) -> Unit)?):  {
        launch(UI) {
            try {
                onSuccess?.let {
                    onSuccess(call.await())
                }
            } catch (httpException: HttpException) {
                // a non-2XX response was received
                defaultError(httpException)
            } catch (t: Throwable) {
                // a networking or data conversion error
                onError?.let {
                    onError(t)
                }
            }
        }
    }}
    */

}
