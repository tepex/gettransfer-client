package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.TransferEntity
import org.koin.core.KoinComponent

interface TransferCache : KoinComponent {

    fun insertAllTransfers(transfers: List<TransferEntity>)

    fun insertTransfer(transfer: TransferEntity)

    fun getTransfer(id: Long): TransferEntity?

    fun getAllTransfers(): Pair<List<TransferEntity>, Int?>

    fun getTransfersArchive(): List<TransferEntity>

    fun getTransfersActive(): List<TransferEntity>

    fun deleteAllTransfers()
}
