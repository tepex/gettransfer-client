package com.kg.gettransfer.remote

import com.kg.gettransfer.data.TransferRemote

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity
import com.kg.gettransfer.remote.model.*

import okhttp3.ResponseBody

import org.koin.core.get
import java.io.InputStream

@Suppress("PreferToOverPairSyntax")
class TransferRemoteImpl : TransferRemote {
    private val core = get<ApiCore>()

    override suspend fun createTransfer(transferNew: TransferNewEntity): TransferEntity {
        val wrapper = TransferNewWrapperModel(transferNew.map())
        // val response = tryPostTransfer(wrapper)
        val response: ResponseModel<TransferWrapperModel> = core.tryTwice { core.api.postTransfer(wrapper) }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.transfer.map()
    }

    override suspend fun cancelTransfer(id: Long, reason: String): TransferEntity {
        val response: ResponseModel<TransferWrapperModel> =
            core.tryTwice(id) { _id -> core.api.cancelTransfer(_id, ReasonModel(reason)) }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.transfer.map()
    }

    override suspend fun getTransfer(id: Long): TransferEntity {
        val response: ResponseModel<TransferWrapperModel> = core.tryTwice(id) { _id -> core.api.getTransfer(_id) }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.transfer.map()
    }

    override suspend fun getAllTransfers(
        role: String,
        page: Int,
        status: String?
    ): Pair<List<TransferEntity>, Int?> {

        val response: ResponseModel<TransfersModel> = core.tryTwice {
            core.api.getAllTransfers(role, page, status)
        }
        @Suppress("UnsafeCallOnNullableType")
        val transfers: List<TransferModel> = response.data!!.transfers
        return Pair(transfers.map { it.map() }, response.data.pagesCount)
    }

    override suspend fun getTransfersArchive(): List<TransferEntity> {
        val response: ResponseModel<TransfersModel> = core.tryTwice { core.api.getTransfersArchive() }
        @Suppress("UnsafeCallOnNullableType")
        val transfers: List<TransferModel> = response.data!!.transfers
        return transfers.map { it.map() }
    }

    override suspend fun getTransfersActive(): List<TransferEntity> {
        val response: ResponseModel<TransfersModel> = core.tryTwice { core.api.getTransfersActive() }
        @Suppress("UnsafeCallOnNullableType")
        val transfers: List<TransferModel> = response.data!!.transfers
        return transfers.map { it.map() }
    }

    override suspend fun downloadVoucher(transferId: Long): InputStream {
        val response: ResponseBody = core.tryTwice { core.api.downloadVoucher(transferId) }
        return response.byteStream()
    }

    override suspend fun sendAnalytics(transferId: Long, role: String) {
        core.tryTwice { core.api.sendAnalytics(transferId, RoleModel(role)) }
    }
}
