package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.TransferTypeRepository

class TransferTypeInteractor(private val repository: TransferTypeRepository) {
    fun getTransferType() = repository.getTransfersType()
}