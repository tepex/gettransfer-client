package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.ds.SystemCacheDataStore
import com.kg.gettransfer.data.ds.SystemRemoteDataStore

open class SystemDataStoreFactory(private val cacheDataStore: SystemCacheDataStore,
                                  private val remoteDataStore: SystemRemoteDataStore) {
    open fun retrieveCacheDataStore() = cacheDataStore
    open fun retrieveRemoteDataStore() = remoteDataStore
}
