package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.ds.CarrierTripCacheDataStore
import com.kg.gettransfer.data.ds.CarrierTripRemoteDataStore

open class CarrierTripDataStoreFactory(private val cacheDataStore: CarrierTripCacheDataStore,
                                       private val remoteDataStore: CarrierTripRemoteDataStore) {
    open fun retrieveCacheDataStore() = cacheDataStore
    open fun retrieveRemoteDataStore() = remoteDataStore
}
