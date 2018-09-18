package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Trip

import com.kg.gettransfer.domain.repository.ApiRepository

class TransferInteractor(private val repository: ApiRepository) {
    lateinit var transfer: Transfer
    
    private var allTransfers: List<Transfer>? = null
    private var activeTransfers: List<Transfer>? = null
    private var completedTransfers: List<Transfer>? = null

    suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean) = 
        repository.getRouteInfo(from, to, withPrices, returnWay)

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
        return transfer
    }
    
    suspend fun getOffers() = repository.getOffers(transfer.id)
    
    suspend fun getAllTransfers(): List<Transfer> {
        if(allTransfers == null) allTransfers = repository.getAllTransfers()
        return allTransfers!!
    }

    suspend fun getActiveTransfers(): List<Transfer> {
        if(activeTransfers == null) activeTransfers = repository.getTransfersActive()
        return activeTransfers!!
    }

    suspend fun getCompletedTransfers(): List<Transfer> {
        if(completedTransfers == null) completedTransfers = repository.getTransfersArchive()
        return completedTransfers!!
    }
    
    fun invalidate() {
        allTransfers = null
        activeTransfers = null
        completedTransfers = null
    }

    suspend fun getTransfer(transferId: Long) = repository.getTransfer(transferId)
    suspend fun cancelTransfer(transferId: Long, reason: String) = repository.cancelTransfer(transferId, reason)
}
