package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew

import com.kg.gettransfer.domain.repository.TransferRepository

class TransferInteractor(private val repository: TransferRepository) {

    suspend fun createTransfer(transferNew: TransferNew): Result<Transfer> {
        return repository.createTransfer(transferNew)
    }

    suspend fun getTransfer(id: Long, fromCache: Boolean = false, role: String = "passenger") =
            when(fromCache) {
                false -> repository.getTransfer(id, role)
                true -> repository.getTransferCached(id, role)
            }

    suspend fun cancelTransfer(id: Long, reason: String) = repository.cancelTransfer(id, reason)
        /*val cancelledTransfer = repository.cancelTransfer(transfer!!.id, reason)
        if(allTransfers != null) allTransfers!!.map { if(it.id == transfer!!.id) it.status = cancelledTransfer.status }*/
    suspend fun setOffersUpdatedDate(id: Long) = repository.setOffersUpdateDate(id)

    suspend fun getAllTransfers() = repository.getAllTransfers()
    suspend fun getTransfersActiveCached() = repository.getTransfersActiveCached()
    suspend fun getTransfersActive() = repository.getTransfersActive()
    suspend fun getTransfersArchiveCached() = repository.getTransfersArchiveCached()
    suspend fun getTransfersArchive() = repository.getTransfersArchive()

    fun clearTransfersCache(): Result<Unit> {
        repository.clearTransfersCache()
        return Result(Unit)
    }
}
