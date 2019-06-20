package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PromoDataStore
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.PromoDataStoreCache
import com.kg.gettransfer.data.ds.PromoDataStoreRemote

import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.PromoDiscount
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.PromoRepository

import org.koin.standalone.get

class PromoRepositoryImpl(
    private val factory: DataStoreFactory<PromoDataStore, PromoDataStoreCache, PromoDataStoreRemote>
) : PromoRepository {

    override suspend fun getDiscount(code: String): Result<PromoDiscount> =
        try {
            Result(factory.retrieveRemoteDataStore().getDiscount(code).map())
        }
        catch (e: RemoteException) {
            Result(PromoDiscount(""), e.map())
        }
}
