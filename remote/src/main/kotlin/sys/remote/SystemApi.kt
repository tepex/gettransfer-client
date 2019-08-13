package com.kg.gettransfer.sys.remote

import com.kg.gettransfer.remote.model.ResponseModel

import kotlinx.coroutines.Deferred

import retrofit2.http.GET

interface SystemApi {
    @GET(API_CONFIGS)
    fun getConfigs(): Deferred<ResponseModel<ConfigsModel>>

    @GET(MOBILE_CONFIGS)
    fun getMobileConfigs(): Deferred<MobileConfigsModel>

    companion object {
        const val API_CONFIGS = "/api/configs"
        const val MOBILE_CONFIGS = "/mobile/mobile.conf"
    }
}
