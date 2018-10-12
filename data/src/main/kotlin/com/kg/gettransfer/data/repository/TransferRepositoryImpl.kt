package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.model.TransferNew

import com.kg.gettransfer.domain.repository.TransferRepository

class TransferRepositoryImpl(private val apiRepository: ApiRepositoryImpl): TransferRepository {
    override suspend fun createTransfer(transferNew: TransferNew) = apiRepository.createTransfer(transferNew)
    override suspend fun cancelTransfer(id: Long, reason: String) = apiRepository.cancelTransfer(id, reason)
    override suspend fun getAllTransfers() = apiRepository.getAllTransfers()
	override suspend fun getTransfer(id: Long) = apiRepository.getTransfer(id)
    override suspend fun getTransfersArchive() = apiRepository.getTransfersActive()
    override suspend fun getTransfersActive() = apiRepository.getTransfersActive()
}
