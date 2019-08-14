package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew

import com.kg.gettransfer.domain.repository.TransferRepository

class TransferInteractor(private val repository: TransferRepository) {

    suspend fun createTransfer(transferNew: TransferNew) = repository.createTransfer(transferNew)

    suspend fun getTransfer(id: Long, fromCache: Boolean = false, role: String = "passenger") =
        if (fromCache) repository.getTransferCached(id, role) else repository.getTransfer(id, role)

    suspend fun cancelTransfer(id: Long, reason: String) = repository.cancelTransfer(id, reason)

    suspend fun setOffersUpdatedDate(id: Long) = repository.setOffersUpdateDate(id)

    suspend fun getAllTransfers() = repository.getAllTransfers()
    suspend fun getTransfersActive() = repository.getTransfersActive()
    suspend fun getTransfersArchive() = repository.getTransfersArchive()

    fun clearTransfersCache(): Result<Unit> {
        repository.clearTransfersCache()
        return Result(Unit)
    }

    suspend fun downloadVoucher(transferId: Long) = repository.downloadVoucher(transferId)

    suspend fun sendAnalytics(transferId: Long, role: String) = repository.sendAnalytics(transferId, role)
}
