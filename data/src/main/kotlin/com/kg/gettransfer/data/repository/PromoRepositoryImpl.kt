package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ds.PromoDataStoreFactory
import com.kg.gettransfer.data.mapper.PromoDiscountMapper
import com.kg.gettransfer.domain.model.PromoDiscount
import com.kg.gettransfer.domain.repository.PromoRepository

class PromoRepositoryImpl(val mapper: PromoDiscountMapper,
                          val factory: PromoDataStoreFactory): PromoRepository{
    override suspend fun getDiscount(code: String): PromoDiscount {
        val entity = factory.retrieveRemoteStore().getDiscount(code)
        return mapper.fromEntity(entity)
    }

}