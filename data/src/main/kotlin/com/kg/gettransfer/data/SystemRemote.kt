package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity

import com.kg.gettransfer.sys.data.MobileConfigsEntity

import org.koin.core.KoinComponent

interface SystemRemote : KoinComponent {

    suspend fun getConfigs(): ConfigsEntity

    suspend fun getMobileConfigs(): MobileConfigsEntity

    fun changeEndpoint(endpoint: EndpointEntity)
}
