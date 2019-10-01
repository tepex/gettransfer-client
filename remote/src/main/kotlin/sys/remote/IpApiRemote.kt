package com.kg.gettransfer.sys.remote

import com.kg.gettransfer.remote.ApiCore
import com.kg.gettransfer.sys.data.IpApiRemoteDataSource

import org.koin.core.KoinComponent
import org.koin.core.inject

class IpApiRemote : IpApiRemoteDataSource, KoinComponent {

    private val apiCore: ApiCore by inject()

    override suspend fun put(data: String) {
        apiCore.changeIpApiKey(data)
    }

    override suspend fun clear() {
        error("Method `clear()` is prohibited in IpApiRemote")
    }
}
