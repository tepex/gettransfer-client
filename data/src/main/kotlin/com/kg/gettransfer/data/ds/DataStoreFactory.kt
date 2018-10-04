package com.kg.gettransfer.data.ds

open class DataStoreFactory<C, R>(private val cacheDataStore: C,
                                  private val remoteDataStore: R) {
    open fun retrieveCacheDataStore(): C = cacheDataStore
    open fun retrieveRemoteDataStore(): R = remoteDataStore
}
