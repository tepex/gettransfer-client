package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity

import com.kg.gettransfer.sys.data.MobileConfigsEntity

import org.koin.core.KoinComponent

interface SystemDataStore : KoinComponent {

    suspend fun getConfigs(): ConfigsEntity?

    suspend fun setConfigs(configsEntity: ConfigsEntity)

    suspend fun getMobileConfigs(): MobileConfigsEntity?

    suspend fun setMobileConfigs(mobileConfigsEntity: MobileConfigsEntity)

    fun changeEndpoint(endpoint: EndpointEntity)
}
