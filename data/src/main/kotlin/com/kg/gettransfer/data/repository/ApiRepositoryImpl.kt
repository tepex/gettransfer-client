package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.Api
import com.kg.gettransfer.domain.repository.ApiRepository

class ApiRepositoryImpl(private val api: Api): ApiRepository {
	
	init {
		
	}
	
	override fun qqq(): String = "sdfsdf"
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
