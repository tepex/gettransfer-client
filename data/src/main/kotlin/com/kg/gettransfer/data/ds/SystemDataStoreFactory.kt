package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SystemDataStore

open class SystemDataStoreFactory(private val cacheDataStore: SystemCacheDataStore,
                                  private val remoteDataStore: SystemRemoteDataStore) {
    open fun retrieveCacheDataStore(): SystemDataStore = cacheDataStore
    open fun retrieveRemoteDataStore(): SystemDataStore = remoteDataStore
}
