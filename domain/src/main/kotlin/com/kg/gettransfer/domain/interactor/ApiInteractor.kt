package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.ApiRepository

class ApiInteractor(private val repository: ApiRepository) {
	lateinit var activeTransfers: List<Transfer>
	lateinit var allTransfers: List<Transfer>
	lateinit var completedTransfers: List<Transfer>

	suspend fun coldStart() { repository.coldStart() }
	fun getConfigs() = repository.getConfigs()
	fun getAccount() = repository.getAccount()
	
	/* Not used now.
	suspend fun createAccount(account: Account) { repository.createAccount(account) }
	*/
	
	suspend fun getAllTransfers(): List<Transfer> = repository.getAllTransfers()
	suspend fun getTransfer(idTransfer: Long): Transfer = repository.getTransfer(idTransfer)
	suspend fun getTransfersArchive(): List<Transfer> = repository.getTransfersArchive()
	suspend fun getTransfersActive(): List<Transfer> = repository.getTransfersActive()
	suspend fun getOffers(idTransfer: Long): List<Offer> = repository.getOffers(idTransfer)
    suspend fun createTransfer(from: GTAddress,
                               to: GTAddress,
                               tripTo: Trip,
                               tripReturn: Trip?,
                               transportTypes: List<String>,
                               pax: Int,
                               childSeats: Int?,
                               passengerOfferedPrice: Int?,
                               nameSign: String,
                               comment: String?,
                               account: Account,
                               promoCode: String?,
                               paypalOnly: Boolean) = 
        repository.createTransfer(from, to, tripTo, tripReturn, transportTypes, pax,
                                  childSeats, passengerOfferedPrice, nameSign, comment,
                                  account, promoCode, paypalOnly)
	suspend fun cancelTransfer(idTransfer: Long, reason: String): Transfer = repository.cancelTransfer(idTransfer, reason)
	fun getLastTransfer(): Transfer = repository.getLastTransfer()
}
