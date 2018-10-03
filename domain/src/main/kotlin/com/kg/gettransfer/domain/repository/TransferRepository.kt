package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Trip

interface TransferRepository {
    
    suspend fun getAllTransfers(): List<Transfer>
    suspend fun getTransfer(id: Long): Transfer
    suspend fun getTransfersArchive(): List<Transfer>
    suspend fun getTransfersActive(): List<Transfer>
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
                               profile: Profile,
                               promoCode: String?,
                               paypalOnly: Boolean): Transfer
    suspend fun cancelTransfer(id: Long, reason: String): Transfer
}
