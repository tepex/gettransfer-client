package com.kg.gettransfer.domain.interactor

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
    
    suspend fun createTransfer(transferNew: TransferNew): Transfer {
        transfer = repository.createTransfer(transferNew)
        selectedId = transfer!!.id
        return transfer!!
    }

    suspend fun getTransfer(id: Long): Transfer {
        if(transfer?.id == id) return transfer!!
        transfer = repository.getTransfer(id)
        return transfer!!
    }
    
    suspend fun cancelTransfer(reason: String) {
        /*val cancelledTransfer = repository.cancelTransfer(transfer!!.id, reason)
        if(allTransfers != null) allTransfers!!.map { if(it.id == transfer!!.id) it.status = cancelledTransfer.status }*/
        repository.cancelTransfer(transfer!!.id, reason)
    }
    
    suspend fun getAllTransfers(): List<Transfer> {
        if(allTransfers == null) allTransfers = repository.getAllTransfers()
        //insertNewTransfer()
        return allTransfers!!
    }

    fun deleteAllTransfersList(){
        allTransfers = null
    }

    suspend fun getActiveTransfers(): List<Transfer> {
        /*if(activeTransfers == null) */activeTransfers = getAllTransfers().filter {
                it.status == Transfer.STATUS_NEW ||
                it.status == Transfer.STATUS_DRAFT ||
                it.status == Transfer.STATUS_PERFORMED ||
                it.status == Transfer.STATUS_PENDING
        }
        return activeTransfers!!
    }

    suspend fun getCompletedTransfers(): List<Transfer> {
        /*if(completedTransfers == null) */completedTransfers = getAllTransfers().filter {
                it.status == Transfer.STATUS_COMPLETED ||
                it.status == Transfer.STATUS_NOT_COMPLETED
            }
        return completedTransfers!!
    }

    suspend fun getArchivedTransfers(): List<Transfer> {
        /*if(archivedTransfers == null) */archivedTransfers = getAllTransfers().filter {
                    it.status != Transfer.STATUS_COMPLETED ||
                    it.status != Transfer.STATUS_NOT_COMPLETED
        }
        return archivedTransfers!!
    }
    
    /*private fun insertNewTransfer() {
        if(allTransfers == null || transfer == null || allTransfers!!.firstOrNull()?.id == transfer!!.id) return
        val mutableList = allTransfers!!.toMutableList()
        mutableList.add(0, transfer!!)
        allTransfers = mutableList
    }*/
}
