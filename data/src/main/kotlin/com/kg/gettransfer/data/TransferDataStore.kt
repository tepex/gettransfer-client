package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity
import org.koin.core.KoinComponent
import java.io.InputStream

interface TransferDataStore : KoinComponent {

    suspend fun createTransfer(transferNew: TransferNewEntity): TransferEntity

    suspend fun cancelTransfer(id: Long, reason: String): TransferEntity

    suspend fun getTransfer(id: Long, role: String): TransferEntity?

    suspend fun getAllTransfers(): List<TransferEntity>

    suspend fun getTransfersArchive(): List<TransferEntity>

    suspend fun getTransfersActive(): List<TransferEntity>

    fun clearTransfersCache()

    suspend fun downloadVoucher(transferId: Long): InputStream

    suspend fun sendAnalytics(transferId: Long, role: String)
}
