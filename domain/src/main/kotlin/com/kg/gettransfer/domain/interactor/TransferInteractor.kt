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

    suspend fun getAllTransfers() = repository.getAllTransfers()
    suspend fun getTransfersActiveCached() = repository.getTransfersActiveCached()
    suspend fun getTransfersActive() = repository.getTransfersActive()
    suspend fun getTransfersArchiveCached() = repository.getTransfersArchiveCached()
    suspend fun getTransfersArchive() = repository.getTransfersArchive()

    fun clearTransfersCache(): Result<Unit> {
        repository.clearTransfersCache()
        return Result(Unit)
    }

    suspend fun getActiveTransfers(): Result<List<Transfer>> {
        /*if(activeTransfers == null) */
        val result = getAllTransfers()
        if(result.isError() && !result.fromCache) return result
        val filteredList = result.model.filter {
                it.status == Transfer.Status.NEW ||
                it.status == Transfer.Status.DRAFT ||
                it.status == Transfer.Status.PERFORMED ||
                it.status == Transfer.Status.PENDING_CONFIRMATION
        }
        return Result(filteredList)
    }

    suspend fun getCompletedTransfers(): Result<List<Transfer>> {
        /*if(completedTransfers == null) */
        val result = getAllTransfers()
        if(result.isError() && !result.fromCache) return result
        val filteredList = result.model.filter {
                it.status == Transfer.Status.COMPLETED ||
                it.status == Transfer.Status.NOT_COMPLETED
            }
        return Result(filteredList)
    }

    suspend fun getArchivedTransfers(): Result<List<Transfer>> {
        /*if(archivedTransfers == null) */
        val result = getAllTransfers()
        if(result.isError() && !result.fromCache) return result
        val filteredList = result.model.filter {
                    it.status != Transfer.Status.COMPLETED ||
                    it.status != Transfer.Status.NOT_COMPLETED
        }
        return Result(filteredList)
    }

    /*private fun insertNewTransfer() {
        if(allTransfers == null || transfer == null || allTransfers!!.firstOrNull()?.id == transfer!!.id) return
        val mutableList = allTransfers!!.toMutableList()
        mutableList.add(0, transfer!!)
        allTransfers = mutableList
    }*/
}
