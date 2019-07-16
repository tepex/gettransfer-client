package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew
import java.io.InputStream

interface TransferRepository {
    suspend fun getAllTransfers(): Result<List<Transfer>>
    suspend fun getTransfer(id: Long, role: String): Result<Transfer>
    suspend fun getTransferCached(id: Long, role: String): Result<Transfer>
    suspend fun getTransfersArchive(): Result<List<Transfer>>
    suspend fun getTransfersActive(): Result<List<Transfer>>
    suspend fun createTransfer(transferNew: TransferNew): Result<Transfer>
    suspend fun cancelTransfer(id: Long, reason: String): Result<Transfer>
    suspend fun setOffersUpdateDate(id: Long): Result<Unit>
    fun clearTransfersCache()
    suspend fun downloadVoucher(transferId: Long): Result<InputStream?>
}
