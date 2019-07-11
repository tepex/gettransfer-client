package com.kg.gettransfer.remote

import com.kg.gettransfer.data.SystemRemote

import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity

import com.kg.gettransfer.remote.model.ConfigsModel
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.map

import com.kg.gettransfer.sys.data.MobileConfigsEntity
import com.kg.gettransfer.sys.remote.MobileConfigsModel
import com.kg.gettransfer.sys.remote.SystemApiImpl
import com.kg.gettransfer.sys.remote.map

import org.koin.core.get

class SystemRemoteImpl : SystemRemote {

    private val core = get<ApiCore>()
    private val system = SystemApiImpl()

    override suspend fun getConfigs(): ConfigsEntity {
        val response: ResponseModel<ConfigsModel> = core.tryTwice { core.api.getConfigs() }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }

    override suspend fun getMobileConfigs(): MobileConfigsEntity {
        val response: MobileConfigsModel = system.api.getMobileConfigs().await()
        return response.map()
    }

    override fun changeEndpoint(endpoint: EndpointEntity) {
        val endpointModel = endpoint.map()
        core.changeEndpoint(endpointModel)
        system.changeEndpoint(endpointModel)
    }
}
