package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew

import com.kg.gettransfer.domain.repository.TransferRepository

class TransferInteractor(private val repository: TransferRepository) {
    var selectedId: Long? = null
    private var transfer: Transfer? = null
    
    private var allTransfers: List<Transfer>? = null
    private var activeTransfers: List<Transfer>? = null
    private var completedTransfers: List<Transfer>? = null
    private var archivedTransfers: List<Transfer>? = null
    
    suspend fun createTransfer(transferNew: TransferNew) =
        repository.createTransfer(transferNew).apply { if(!isError()) selectedId = model.id }

    suspend fun getTransfer(id: Long) = repository.getTransfer(id).apply { if(!isError()) transfer = model }
    
    suspend fun cancelTransfer(reason: String) = repository.cancelTransfer(transfer!!.id, reason)
        /*val cancelledTransfer = repository.cancelTransfer(transfer!!.id, reason)
        if(allTransfers != null) allTransfers!!.map { if(it.id == transfer!!.id) it.status = cancelledTransfer.status }*/
        
    suspend fun getAllTransfers() = repository.getAllTransfers().apply { if(!isError() && allTransfers == null) allTransfers = model}

    fun deleteAllTransfersList() { allTransfers = null }

    suspend fun getActiveTransfers(): Result<List<Transfer>> {
        /*if(activeTransfers == null) */
        val result = getAllTransfers()
        if(result.isError()) return result
        activeTransfers = result.model.filter {
                it.status == Transfer.STATUS_NEW ||
                it.status == Transfer.STATUS_DRAFT ||
                it.status == Transfer.STATUS_PERFORMED ||
                it.status == Transfer.STATUS_PENDING
        }
        return Result(allTransfers!!)
    }

    suspend fun getCompletedTransfers(): Result<List<Transfer>> {
        /*if(completedTransfers == null) */
        val result = getAllTransfers()
        if(result.isError()) return result
        completedTransfers = result.model.filter {
                it.status == Transfer.STATUS_COMPLETED ||
                it.status == Transfer.STATUS_NOT_COMPLETED
            }
        return Result(completedTransfers!!)
    }

    suspend fun getArchivedTransfers(): Result<List<Transfer>> {
        /*if(archivedTransfers == null) */
        val result = getAllTransfers()
        if(result.isError()) return result
        archivedTransfers = result.model.filter {
                    it.status != Transfer.STATUS_COMPLETED ||
                    it.status != Transfer.STATUS_NOT_COMPLETED
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
