package com.kg.gettransfer.remote

import com.kg.gettransfer.data.TransferRemote

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity

import com.kg.gettransfer.remote.mapper.TransferMapper
import com.kg.gettransfer.remote.mapper.TransferNewMapper

import com.kg.gettransfer.remote.model.ReasonModel
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.TransferModel
import com.kg.gettransfer.remote.model.TransferNewWrapperModel
import com.kg.gettransfer.remote.model.TransfersModel
import com.kg.gettransfer.remote.model.TransferWrapperModel

import org.koin.standalone.get

class TransferRemoteImpl : TransferRemote {
    private val core              = get<ApiCore>()
    private val transferMapper    = get<TransferMapper>()
    private val transferNewMapper = get<TransferNewMapper>()

    override suspend fun createTransfer(transferNew: TransferNewEntity): TransferEntity {
        val wrapper = TransferNewWrapperModel(transferNewMapper.toRemote(transferNew))
        //val response = tryPostTransfer(wrapper)
        val response: ResponseModel<TransferWrapperModel> = core.tryTwice { core.api.postTransfer(wrapper) }
        return transferMapper.fromRemote(response.data?.transfer!!)
    }

    /*private suspend fun tryPostTransfer(transferNew: TransferNewWrapperModel): ResponseModel<TransferWrapperModel> {
        return try { core.api.postTransfer(transferNew).await() }
        catch (e: Exception) {
            log.error("Create transfer error", e)
            if (e is RemoteException) throw e *//* second invocation *//*
            val ae = core.remoteException(e)
            if (!ae.isInvalidToken()) throw ae

            try { core.updateAccessToken() } catch (e1: Exception) { throw core.remoteException(e1) }
            return try { core.api.postTransfer(transferNew).await() } catch (e2: Exception) { throw core.remoteException(e2) }
        }
    }*/

    override suspend fun cancelTransfer(id: Long, reason: String): TransferEntity {
        val response: ResponseModel<TransferWrapperModel> = core.tryTwice(id) { _id -> core.api.cancelTransfer(_id, ReasonModel(reason)) }
        return transferMapper.fromRemote(response.data?.transfer!!)
    }

    override suspend fun getTransfer(id: Long): TransferEntity {
        val response: ResponseModel<TransferWrapperModel> = core.tryTwice(id) { _id -> core.api.getTransfer(_id) }
        return transferMapper.fromRemote(response.data?.transfer!!)
    }

    override suspend fun getAllTransfers(): List<TransferEntity> {
        val response: ResponseModel<TransfersModel> = core.tryTwice { core.api.getAllTransfers() }
        val transfers: List<TransferModel> = response.data!!.transfers
        return transfers.map { transferMapper.fromRemote(it) }
    }

    override suspend fun getTransfersArchive(): List<TransferEntity> {
        val response: ResponseModel<TransfersModel> = core.tryTwice { core.api.getTransfersArchive() }
        val transfers: List<TransferModel> = response.data!!.transfers
        return transfers.map { transferMapper.fromRemote(it) }
    }

    override suspend fun getTransfersActive(): List<TransferEntity> {
        val response: ResponseModel<TransfersModel> = core.tryTwice { core.api.getTransfersActive() }
        val transfers: List<TransferModel> = response.data!!.transfers
        return transfers.map { transferMapper.fromRemote(it) }
    }
}
