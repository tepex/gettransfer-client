package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.TransferType

interface TransferTypeRepository {
    fun getTransfersType(): List<TransferType>
}