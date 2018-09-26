package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.*

interface ApiRepository {
    suspend fun coldStart()
    fun getConfigs(): Configs
    fun getAccount(): Account
    suspend fun putAccount(account: Account)
    /* Not used
    suspend fun createAccount(account: Account)
    */
	suspend fun login(email: String, password: String): Account
	fun logout()
	
	suspend fun getRouteInfo(from: String, to: String, withPrices: Boolean, returnWay: Boolean): RouteInfo
	
    suspend fun getAllTransfers(): List<Transfer>
    suspend fun getTransfer(transferId: Long): Transfer
    suspend fun getTransfersArchive(): List<Transfer>
    suspend fun getTransfersActive(): List<Transfer>
    suspend fun getOffers(transferId: Long): List<Offer>
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
                               paypalOnly: Boolean): Transfer
    suspend fun cancelTransfer(transferId: Long, reason: String): Transfer

    suspend fun getCarrierTrips(): List<CarrierTrip>
    suspend fun getCarrierTrip(carrierTripId: Long): CarrierTrip
}
