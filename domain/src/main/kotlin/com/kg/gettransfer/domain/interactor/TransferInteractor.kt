package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Trip

import com.kg.gettransfer.domain.repository.TransferRepository

class TransferInteractor(private val repository: TransferRepository) {
    var selectedId: Long? = null
    private var transfer: Transfer? = null
    
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
                               profile: Profile,
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
                                             profile.name!!,
                                             comment,
                                             profile,
                                             promoCode,
                                             paypalOnly)
        insertNewTransfer()
        return transfer!!
    }

    suspend fun getTransfer(id: Long): Transfer {
        if(transfer?.id == id) return transfer!!
        transfer = repository.getTransfer(id)
        return transfer!!
    }
    
    suspend fun cancelTransfer(reason: String) {
        val cancelledTransfer = repository.cancelTransfer(transfer!!.id, reason)
        if(allTransfers != null) allTransfers!!.map { if(it.id == transfer!!.id) it.status = cancelledTransfer.status }
    }
    
    suspend fun getAllTransfers(): List<Transfer> {
        if(allTransfers == null) allTransfers = repository.getAllTransfers()
        insertNewTransfer()
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
    
    private fun insertNewTransfer() {
        if(allTransfers == null || transfer == null || allTransfers!!.firstOrNull()?.id == transfer!!.id) return
        val mutableList = allTransfers!!.toMutableList()
        mutableList.add(0, transfer!!)
        allTransfers = mutableList
    }
}
