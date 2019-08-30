package com.kg.gettransfer.sys.remote

import com.kg.gettransfer.remote.model.ResponseModel

import retrofit2.http.GET

interface SystemApi {
    @GET(API_CONFIGS)
    suspend fun getConfigs(): ResponseModel<ConfigsModel>

    @GET(MOBILE_CONFIGS)
    suspend fun getMobileConfigs(): MobileConfigsModel

    companion object {
        const val API_CONFIGS = "/api/configs"
        const val MOBILE_CONFIGS = "/mobile/mobile.conf"
    }
}
