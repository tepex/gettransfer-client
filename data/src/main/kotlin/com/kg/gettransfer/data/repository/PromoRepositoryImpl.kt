package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PromoDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.PromoDataStoreCache
import com.kg.gettransfer.data.ds.PromoDataStoreRemote

import com.kg.gettransfer.data.mapper.PromoDiscountMapper
import com.kg.gettransfer.data.model.PromoDiscountEntity

import com.kg.gettransfer.domain.model.PromoDiscount
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.PromoRepository

class PromoRepositoryImpl(private val factory: DataStoreFactory<PromoDataStore, PromoDataStoreCache, PromoDataStoreRemote>,
                          private val mapper: PromoDiscountMapper): BaseRepository(), PromoRepository {
    override suspend fun getDiscount(code: String): Result<PromoDiscount> =
        retrieveRemoteModel<PromoDiscountEntity, PromoDiscount>(mapper) { factory.retrieveRemoteDataStore().getDiscount(code) }
}
