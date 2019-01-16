package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.TransferCache
import com.kg.gettransfer.data.TransferDataStore

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity

import org.koin.standalone.inject

/**
 * Implementation of the [TransferDataStore] interface to provide a means of communicating with the local data source.
 */
@Suppress("UNUSED_PARAMETER")
open class TransferDataStoreCache: TransferDataStore {
    private val cache: TransferCache by inject()
    
    fun addTransfer(transfer: TransferEntity) = cache.insertTransfer(transfer)
    fun addAllTransfers(transfers: List<TransferEntity>) = cache.insertAllTransfers(transfers)
    
    override suspend fun getTransfer(id: Long) = cache.getTransfer(id)
    override suspend fun getAllTransfers() = cache.getAllTransfers()
    override suspend fun getTransfersArchive() = cache.getTransfersArchive()
    override suspend fun getTransfersActive() = cache.getTransfersActive()

    override fun clearTransfersCache() { cache.deleteAllTransfers() }

    override suspend fun createTransfer(transferNew: TransferNewEntity): TransferEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun cancelTransfer(id: Long, reason: String): TransferEntity {
        throw UnsupportedOperationException()
    }
}
