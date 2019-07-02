package com.kg.gettransfer.remote

import com.kg.gettransfer.data.SystemRemote

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.MobileConfigEntity

import com.kg.gettransfer.remote.model.MobileConfigModel
import com.kg.gettransfer.remote.model.map

import org.koin.core.get

class SystemRemoteImpl : SystemRemote {

    private val core = get<ApiCore>()

    override suspend fun getMobileConfigs(): MobileConfigEntity {
        val response: MobileConfigModel = core.tryTwice { core.api.getMobileConfigs() }
        return response.map()
    }

    override fun changeEndpoint(endpoint: EndpointEntity) = core.changeEndpoint(endpoint.map())
}
