package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.Api

import com.kg.gettransfer.data.model.*
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.ApiRepository

import retrofit2.HttpException

import timber.log.Timber

class ApiRepositoryImpl(private val api: Api, private val apiKey: String): ApiRepository {
	
	private var accessToken: String? = null
	
	/* @TODO: Обрабатывать {"result":"error","error":{"type":"wrong_api_key","details":"API key \"ccd9a245018bfe4f386f4045ee4a006fsss\" not found"}} */
	override suspend fun updateToken(): String {
		val response: ApiResponse<ApiToken> = try {
			api.accessToken(apiKey).await()
		} catch(httpException: HttpException) {
			throw httpException
		}
		accessToken = response.data?.token
		Timber.d("access token updated: $accessToken")
		return accessToken!!
	}
	
	override suspend fun configs() {
		if(accessToken == null) updateToken()
		val response: ApiResponse<ApiConfigs> = try {
			api.configs(accessToken!!).await()
		} catch(httpException: HttpException) {
			throw httpException
		}
		val ret: ApiConfigs = response.data!!
		Timber.d("types: %s", ret.transportTypes)
	}
}
