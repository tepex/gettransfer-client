package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.User
import com.kg.gettransfer.domain.model.Trip

import com.kg.gettransfer.domain.repository.TransferRepository

class TransferRepositoryImpl(private val apiRepository: ApiRepositoryImpl): TransferRepository {
    override suspend fun createTransfer(from: GTAddress,
                                        to: GTAddress,
                                        tripTo: Trip,
                                        tripReturn: Trip?,
                                        transportTypes: List<String>,
                                        pax: Int,
                                        childSeats: Int?,
                                        passengerOfferedPrice: Int?,
                                        nameSign: String,
                                        comment: String?,
                                        user: User,
                                        promoCode: String?,
                                        paypalOnly: Boolean) = 
        apiRepository.createTransfer(from,
                                     to,
                                     tripTo,
                                     tripReturn,
                                     transportTypes,
                                     pax,
                                     childSeats,
                                     passengerOfferedPrice,
                                     nameSign,
                                     comment,
                                     user,
                                     promoCode,
                                     paypalOnly)
    override suspend fun cancelTransfer(id: Long, reason: String) = apiRepository.cancelTransfer(id, reason)
    override suspend fun getAllTransfers() = apiRepository.getAllTransfers()
	override suspend fun getTransfer(id: Long) = apiRepository.getTransfer(id)
    override suspend fun getTransfersArchive() = apiRepository.getTransfersActive()
    override suspend fun getTransfersActive() = apiRepository.getTransfersActive()
}
