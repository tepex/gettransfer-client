package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity

import org.koin.standalone.KoinComponent

interface TransferCache: KoinComponent {
    fun insertAllTransfers(transfers: List<TransferEntity>)
    fun insertTransfer(transfer: TransferEntity)

    fun getTransfer(id: Long): TransferEntity
    fun getAllTransfers(): List<TransferEntity>
    fun getTransfersArchive(): List<TransferEntity>
    fun getTransfersActive(): List<TransferEntity>

    fun deleteAllTransfers()
}
