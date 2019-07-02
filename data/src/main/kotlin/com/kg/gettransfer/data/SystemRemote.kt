package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.MobileConfigEntity

import org.koin.core.KoinComponent

interface SystemRemote : KoinComponent {

    suspend fun getConfigs(): ConfigsEntity

    suspend fun getMobileConfigs(): MobileConfigEntity

    fun changeEndpoint(endpoint: EndpointEntity)
}
