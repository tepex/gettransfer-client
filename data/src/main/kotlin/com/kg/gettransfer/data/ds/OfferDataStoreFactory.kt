package com.kg.gettransfer.data.ds

open class OfferDataStoreFactory(private val remoteDataStore: OfferRemoteDataStore,
                                 private val socketDataStore: OfferSocketDataStore) {
    open fun retrieveRemoteDataStore() = remoteDataStore
    open fun retrieveSocketDataStore() = socketDataStore
}
