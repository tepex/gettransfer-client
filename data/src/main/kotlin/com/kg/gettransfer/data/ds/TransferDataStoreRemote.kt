package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.TransferRemote
import com.kg.gettransfer.data.TransferDataStore
import com.kg.gettransfer.data.model.TransferNewEntity
import org.koin.core.inject

/**
 * Implementation of the [TransferDataStore] interface to provide a means of communicating with the remote data source.
 */
open class TransferDataStoreRemote : TransferDataStore {
    private val remote: TransferRemote by inject()

    override suspend fun createTransfer(transferNew: TransferNewEntity) = remote.createTransfer(transferNew)

    override suspend fun cancelTransfer(id: Long, reason: String) = remote.cancelTransfer(id, reason)

    override suspend fun restoreTransfer(id: Long) = remote.restoreTransfer(id)

    override suspend fun getTransfer(id: Long) = remote.getTransfer(id)

    override suspend fun getAllTransfers(
        role: String,
        page: Int,
        status: String?
    ) = remote.getAllTransfers(role, page, status)

    override suspend fun getTransfersArchive() = remote.getTransfersArchive()

    override suspend fun getTransfersActive() = remote.getTransfersActive()

    override fun clearTransfersCache() { throw UnsupportedOperationException() }

    override suspend fun downloadVoucher(transferId: Long) = remote.downloadVoucher(transferId)

    override suspend fun sendAnalytics(transferId: Long, role: String) = remote.sendAnalytics(transferId, role)
}
