package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity
import org.koin.core.KoinComponent
import java.io.InputStream

interface TransferRemote : KoinComponent {

    suspend fun createTransfer(transferNew: TransferNewEntity): TransferEntity

    suspend fun cancelTransfer(id: Long, reason: String): TransferEntity

    suspend fun getTransfer(id: Long): TransferEntity

    suspend fun getAllTransfers(role: String,
                                page: Int,
                                status: String?): Pair<List<TransferEntity>, Int?>

    suspend fun getTransfersArchive(): List<TransferEntity>

    suspend fun getTransfersActive(): List<TransferEntity>

    suspend fun downloadVoucher(transferId: Long): InputStream

    suspend fun sendAnalytics(transferId: Long, role: String)
}
