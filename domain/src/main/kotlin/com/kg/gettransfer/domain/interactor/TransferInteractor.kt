package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew

import com.kg.gettransfer.domain.repository.TransferRepository

@Suppress("TooManyFunctions")
class TransferInteractor(private val repository: TransferRepository) {

    suspend fun createTransfer(transferNew: TransferNew) = repository.createTransfer(transferNew)

    suspend fun getTransfer(id: Long, fromCache: Boolean = false) =
        if (fromCache) repository.getTransferCached(id) else repository.getTransfer(id)

    suspend fun cancelTransfer(id: Long, reason: String) = repository.cancelTransfer(id, reason)

    suspend fun setOffersUpdatedDate(id: Long) = repository.setOffersUpdateDate(id)

    suspend fun getAllTransfers(role: String = Transfer.Role.PASSENGER.toString(),
                                status: String = "active",
                                page: Int = 1,
                                perPage: Int = 10) = repository.getAllTransfers(role, status, page, perPage)
    suspend fun getTransfersActive() = repository.getTransfersActive()
    suspend fun getTransfersArchive() = repository.getTransfersArchive()

    fun clearTransfersCache(): Result<Unit> {
        repository.clearTransfersCache()
        return Result(Unit)
    }

    suspend fun downloadVoucher(transferId: Long) = repository.downloadVoucher(transferId)

    suspend fun sendAnalytics(transferId: Long, role: String) = repository.sendAnalytics(transferId, role)

    suspend fun isOfferPaid(transferId: Long): Result<Pair<Boolean, Transfer?>> {
        getTransfer(transferId).isSuccess()?.let { transfer ->
            val isOfferPaid = transfer.status == Transfer.Status.PERFORMED || transfer.paidPercentage > 0
            return Result(isOfferPaid to transfer)
        }
        return Result(false to null)
    }
}
