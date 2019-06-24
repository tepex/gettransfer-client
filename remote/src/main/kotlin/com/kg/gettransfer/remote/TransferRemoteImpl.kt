package com.kg.gettransfer.remote

import com.kg.gettransfer.data.TransferRemote

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity

import com.kg.gettransfer.remote.model.ReasonModel
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.TransferModel
import com.kg.gettransfer.remote.model.TransferNewWrapperModel
import com.kg.gettransfer.remote.model.TransfersModel
import com.kg.gettransfer.remote.model.TransferWrapperModel
import com.kg.gettransfer.remote.model.map

import org.koin.standalone.get

class TransferRemoteImpl : TransferRemote {
    private val core              = get<ApiCore>()

    override suspend fun createTransfer(transferNew: TransferNewEntity): TransferEntity {
        val wrapper = TransferNewWrapperModel(transferNew.map())
        //val response = tryPostTransfer(wrapper)
        val response: ResponseModel<TransferWrapperModel> = core.tryTwice { core.api.postTransfer(wrapper) }
        return response.data?.transfer!!.map()
    }

    override suspend fun cancelTransfer(id: Long, reason: String): TransferEntity {
        val response: ResponseModel<TransferWrapperModel> = core.tryTwice(id) { _id -> core.api.cancelTransfer(_id, ReasonModel(reason)) }
        return response.data?.transfer!!.map()
    }

    override suspend fun getTransfer(id: Long, role: String): TransferEntity {
        val response: ResponseModel<TransferWrapperModel> = core.tryTwice(id) { _id -> core.api.getTransfer(_id, role) }
        return response.data?.transfer!!.map()
    }

    override suspend fun getAllTransfers(): List<TransferEntity> {
        val response: ResponseModel<TransfersModel> = core.tryTwice { core.api.getAllTransfers() }
        val transfers: List<TransferModel> = response.data!!.transfers
        return transfers.map { it.map() }
    }

    override suspend fun getTransfersArchive(): List<TransferEntity> {
        val response: ResponseModel<TransfersModel> = core.tryTwice { core.api.getTransfersArchive() }
        val transfers: List<TransferModel> = response.data!!.transfers
        return transfers.map { it.map() }
    }

    override suspend fun getTransfersActive(): List<TransferEntity> {
        val response: ResponseModel<TransfersModel> = core.tryTwice { core.api.getTransfersActive() }
        val transfers: List<TransferModel> = response.data!!.transfers
        return transfers.map { it.map() }
    }
}
