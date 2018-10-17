package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity

interface TransferCache {
    suspend fun createTransfer(transferNew: TransferNewEntity): TransferEntity
    suspend fun cancelTransfer(id: Long, reason: String): TransferEntity
    suspend fun getTransfer(id: Long): TransferEntity
    suspend fun getAllTransfers(): List<TransferEntity>
    suspend fun getTransfersArchive(): List<TransferEntity>
    suspend fun getTransfersActive(): List<TransferEntity>
}
