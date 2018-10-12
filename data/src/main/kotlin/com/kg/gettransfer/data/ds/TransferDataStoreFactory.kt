package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.ds.TransferCacheDataStore
import com.kg.gettransfer.data.ds.TransferRemoteDataStore

open class TransferDataStoreFactory(private val cacheDataStore: TransferCacheDataStore,
                                    private val remoteDataStore: TransferRemoteDataStore) {
    open fun retrieveCacheDataStore() = cacheDataStore
    open fun retrieveRemoteDataStore() = remoteDataStore
}
