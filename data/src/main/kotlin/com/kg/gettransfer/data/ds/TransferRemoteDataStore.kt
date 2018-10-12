package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.TransferRemote
import com.kg.gettransfer.data.TransferDataStore

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity

/**
 * Implementation of the [TransferDataStore] interface to provide a means of communicating with the remote data source.
 */
open class TransferRemoteDataStore(private val remote: TransferRemote): TransferDataStore {
    override suspend fun createTransfer(transferNew: TransferNewEntity) = remote.createTransfer(transferNew)
    override suspend fun cancelTransfer(id: Long, reason: String) = remote.cancelTransfer(id, reason)
    override suspend fun getTransfer(id: Long) = remote.getTransfer(id)
    override suspend fun getAllTransfers() = remote.getAllTransfers()
    override suspend fun getTransfersArchive() = remote.getTransfersArchive()
    override suspend fun getTransfersActive() = remote.getTransfersActive()
}
