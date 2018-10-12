package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew

interface TransferRepository {
    suspend fun getAllTransfers(): List<Transfer>
    suspend fun getTransfer(id: Long): Transfer
    suspend fun getTransfersArchive(): List<Transfer>
    suspend fun getTransfersActive(): List<Transfer>
    suspend fun createTransfer(transferNew: TransferNew): Transfer
    suspend fun cancelTransfer(id: Long, reason: String): Transfer
}
