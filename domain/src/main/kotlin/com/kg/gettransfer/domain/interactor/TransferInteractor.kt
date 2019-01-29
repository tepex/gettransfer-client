package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.TransferEventListener
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew

import com.kg.gettransfer.domain.repository.TransferRepository

class TransferInteractor(private val repository: TransferRepository) {
    var transfer: Transfer? = null
    var transferNew: TransferNew? = null

    private var allTransfers: List<Transfer>? = null
    private var activeTransfers: List<Transfer>? = null
    private var completedTransfers: List<Transfer>? = null
    private var archivedTransfers: List<Transfer>? = null

    suspend fun createTransfer(transferNew: TransferNew): Result<Transfer> {
        this.transferNew = transferNew
        return repository.createTransfer(transferNew)
    }

    suspend fun getTransfer(id: Long, fromCache: Boolean = false) =
            when(fromCache) {
                false -> repository.getTransfer(id)
                true -> repository.getTransferCached(id)
            }.apply { transfer = model }

    suspend fun cancelTransfer(id: Long, reason: String) = repository.cancelTransfer(id, reason)
        /*val cancelledTransfer = repository.cancelTransfer(transfer!!.id, reason)
        if(allTransfers != null) allTransfers!!.map { if(it.id == transfer!!.id) it.status = cancelledTransfer.status }*/

    suspend fun getAllTransfers() = repository.getAllTransfers().apply { if(allTransfers == null) allTransfers = model}

    fun clearTransfersCache(): Result<Unit> {
        repository.clearTransfersCache()
        return Result(Unit)
    }

    fun deleteAllTransfersList() { allTransfers = null }

    suspend fun getActiveTransfers(): Result<List<Transfer>> {
        /*if(activeTransfers == null) */
        val result = getAllTransfers()
        if(result.isError() && !result.fromCache) return result
        activeTransfers = result.model.filter {
                it.status == Transfer.Status.NEW ||
                it.status == Transfer.Status.DRAFT ||
                it.status == Transfer.Status.PERFORMED ||
                it.status == Transfer.Status.PENDING_CONFIRMATION
        }
        return Result(activeTransfers!!)
    }

    suspend fun getCompletedTransfers(): Result<List<Transfer>> {
        /*if(completedTransfers == null) */
        val result = getAllTransfers()
        if(result.isError() && !result.fromCache) return result
        completedTransfers = result.model.filter {
                it.status == Transfer.Status.COMPLETED ||
                it.status == Transfer.Status.NOT_COMPLETED
            }
        return Result(completedTransfers!!)
    }

    suspend fun getArchivedTransfers(): Result<List<Transfer>> {
        /*if(archivedTransfers == null) */
        val result = getAllTransfers()
        if(result.isError() && !result.fromCache) return result
        archivedTransfers = result.model.filter {
                    it.status != Transfer.Status.COMPLETED ||
                    it.status != Transfer.Status.NOT_COMPLETED
        }
        return Result(archivedTransfers!!)
    }

    /*private fun insertNewTransfer() {
        if(allTransfers == null || transfer == null || allTransfers!!.firstOrNull()?.id == transfer!!.id) return
        val mutableList = allTransfers!!.toMutableList()
        mutableList.add(0, transfer!!)
        allTransfers = mutableList
    }*/
}
