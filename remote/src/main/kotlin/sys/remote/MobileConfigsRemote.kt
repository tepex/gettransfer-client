package com.kg.gettransfer.sys.remote

import com.kg.gettransfer.core.data.ResultData

import com.kg.gettransfer.sys.data.MobileConfigsEntity
import com.kg.gettransfer.sys.data.MobileConfigsRemoteDataSource

import org.koin.core.KoinComponent
import org.koin.core.inject

class MobileConfigsRemote : MobileConfigsRemoteDataSource, KoinComponent {

    private val apiWrapper: SystemApiWrapper by inject()

    override suspend fun getResult(): ResultData<MobileConfigsEntity> {
        apiWrapper.checkApiInitialization()
        return runCatching<MobileConfigsModel> { apiWrapper.api.getMobileConfigs() }.fold(
            { ResultData.Success(it.map()) },
            { ResultData.NetworkError(it) }
        )
    }
}
