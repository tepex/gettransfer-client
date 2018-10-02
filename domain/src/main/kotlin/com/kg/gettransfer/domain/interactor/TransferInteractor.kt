package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Trip

import com.kg.gettransfer.domain.repository.TransferRepository

class TransferInteractor(private val repository: TransferRepository) {
    var selectedId: Long = -1
    var transfer: Transfer? = null
    
    private var allTransfers: List<Transfer>? = null
    private var activeTransfers: List<Transfer>? = null
    private var completedTransfers: List<Transfer>? = null
    private var archivedTransfers: List<Transfer>? = null
    
    suspend fun createTransfer(from: GTAddress,
                               to: GTAddress,
                               tripTo: Trip,
                               tripReturn: Trip?,
                               transportTypes: List<String>,
                               pax: Int,
                               childSeats: Int?,
                               passengerOfferedPrice: Int?,
                               comment: String?,
                               account: Account,
                               promoCode: String?,
                               paypalOnly: Boolean): Transfer {
        transfer = repository.createTransfer(from,
                                             to,
                                             tripTo,
                                             tripReturn,
                                             transportTypes,
                                             pax,
                                             childSeats,
                                             passengerOfferedPrice,
                                             account.fullName!!,
                                             comment,
                                             account,
                                             promoCode,
                                             paypalOnly)
        if(allTransfers != null){
            val muttableList = allTransfers!!.toMutableList()
            muttableList.add(0, transfer!!)
            allTransfers = muttableList
        }
        selectedId = transfer!!.id
        return transfer!!
    }
    
    suspend fun getTransfer() = repository.getTransfer(selectedId)
    suspend fun getOffers() = repository.getOffers(selectedId)
    suspend fun cancelTransfer(reason: String){
        val cancelledTransfer = repository.cancelTransfer(selectedId, reason)
        if(allTransfers != null){
            allTransfers!!.map { if(it.id == selectedId) it.status = cancelledTransfer.status }
        }
    }
    
    suspend fun getAllTransfers(): List<Transfer> {
        if(allTransfers == null) allTransfers = repository.getAllTransfers()
        transfer?.let {
            if(allTransfers!!.firstOrNull()?.id != it.id) {
                val mutableList = allTransfers!!.toMutableList()
                mutableList.add(0, it)
                allTransfers = mutableList
            }
        }
        return allTransfers!!
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
}
