package com.kg.gettransfer.sys.remote

import com.kg.gettransfer.core.data.ResultData

import com.kg.gettransfer.core.remote.processError

import com.kg.gettransfer.remote.model.ResponseModel

import com.kg.gettransfer.sys.data.ConfigsEntity
import com.kg.gettransfer.sys.data.ConfigsRemoteDataSource

import org.koin.core.KoinComponent
import org.koin.core.inject

class ConfigsRemote : ConfigsRemoteDataSource, KoinComponent {

    private val apiWrapper: SystemApiWrapper by inject()

    override suspend fun getResult(): ResultData<ConfigsEntity> =
        runCatching<ResponseModel<ConfigsModel>> { apiWrapper.api.getConfigs() }.fold(
            { responseModel ->
                if (responseModel.data != null) {
                    /* ConfigsModel => ResultData<ConfigsEntity> */
                    ResultData.Success(responseModel.data.map())
                } else {
                    error("Expecting data field in server response: $responseModel")
                }
            },
            ::onFailure
        )

    private fun onFailure(exception: Throwable) = processError(apiWrapper.gson, exception)
}
