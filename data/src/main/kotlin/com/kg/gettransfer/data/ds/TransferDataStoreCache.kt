package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.TransferCache
import com.kg.gettransfer.data.TransferDataStore

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity

/**
 * Implementation of the [TransferDataStore] interface to provide a means of communicating with the local data source.
 */
@Suppress("UNUSED_PARAMETER")
open class TransferDataStoreCache(/*private val cache: TransferCache*/): TransferDataStore {
    override suspend fun createTransfer(transferNew: TransferNewEntity): TransferEntity {
        throw UnsupportedOperationException()
    }
    
    fun addTransfer(transfer: TransferEntity): TransferEntity {
        throw UnsupportedOperationException()
    }
    
    override suspend fun cancelTransfer(id: Long, reason: String): TransferEntity {
        throw UnsupportedOperationException()
    }
    
    override suspend fun getTransfer(id: Long): TransferEntity {
        throw UnsupportedOperationException()
    }
    
    override suspend fun getAllTransfers(): List<TransferEntity> {
        throw UnsupportedOperationException()
    }
    
    override suspend fun getTransfersArchive(): List<TransferEntity> {
        throw UnsupportedOperationException()
    }

    override suspend fun getTransfersActive(): List<TransferEntity> {
        throw UnsupportedOperationException()
    }
}
