package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.ApiRepository

class ApiInteractor(private val repository: ApiRepository) {
	lateinit var activeTransfers: List<Transfer>
	lateinit var allTransfers: List<Transfer>
	lateinit var completedTransfers: List<Transfer>

	suspend fun getConfigs(): Configs = repository.getConfigs()
	suspend fun getAccount(): Account = repository.getAccount()
	suspend fun putAccount(account: Account) { repository.putAccount(account) }
	/* Not used now.
	suspend fun createAccount(account: Account) { repository.createAccount(account) }
	*/
	suspend fun login(email: String, password: String): Account = repository.login(email, password)
	suspend fun getRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean):
			RouteInfo = repository.getRouteInfo(points, withPrices, returnWay)
	fun logout() { repository.logout() }
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
	suspend fun cancelTransfer(idTransfer: Long): Transfer = repository.cancelTransfer(idTransfer)
}
