package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Trip

import com.kg.gettransfer.domain.repository.ApiRepository

class TransferInteractor(private val repository: ApiRepository) {
    lateinit var transfer: Transfer

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
}
