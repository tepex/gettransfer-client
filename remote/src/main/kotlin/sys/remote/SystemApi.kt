package com.kg.gettransfer.sys.remote

import kotlinx.coroutines.Deferred

import retrofit2.http.GET

interface SystemApi {

    @GET(MOBILE_CONFIGS)
    fun getMobileConfigs(): Deferred<MobileConfigsModel>

    companion object {
        const val MOBILE_CONFIGS = "/mobile/mobile.conf"
    }
}
