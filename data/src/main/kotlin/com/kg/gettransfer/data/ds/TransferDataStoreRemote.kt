package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.TransferRemote
import com.kg.gettransfer.data.TransferDataStore
import com.kg.gettransfer.data.model.TransferNewEntity
import org.koin.standalone.inject

/**
 * Implementation of the [TransferDataStore] interface to provide a means of communicating with the remote data source.
 */
open class TransferDataStoreRemote : TransferDataStore {
    private val remote: TransferRemote by inject()

    override suspend fun createTransfer(transferNew: TransferNewEntity) = remote.createTransfer(transferNew)
    override suspend fun cancelTransfer(id: Long, reason: String) = remote.cancelTransfer(id, reason)

    override suspend fun getTransfer(id: Long, role: String) = remote.getTransfer(id, role)
    override suspend fun getAllTransfers() = remote.getAllTransfers()
    override suspend fun getTransfersArchive() = remote.getTransfersArchive()
    override suspend fun getTransfersActive() = remote.getTransfersActive()

    override fun clearTransfersCache() { throw UnsupportedOperationException() }
}
