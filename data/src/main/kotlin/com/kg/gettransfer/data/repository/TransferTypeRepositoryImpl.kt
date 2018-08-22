package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.model.TransferType
import com.kg.gettransfer.domain.repository.TransferTypeRepository

class TransferTypeRepositoryImpl: TransferTypeRepository {
    override fun getTransfersType(): List<TransferType> {
        val listTypesName = arrayListOf("economy", "business", "premium", "van", "minibus", "bus", "limousine", "helicopter")
        val listTypes = ArrayList<TransferType>()
        for(i in 0..listTypesName.size - 1 step 1) listTypes.add(TransferType(listTypesName[i], false, i, i, i))
        return listTypes
    }

}