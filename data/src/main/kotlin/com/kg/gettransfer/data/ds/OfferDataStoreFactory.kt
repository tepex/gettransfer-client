package com.kg.gettransfer.data.ds

open class OfferDataStoreFactory(private val remoteDataStore: OfferRemoteDataStore) {
    open fun retrieveRemoteDataStore() = remoteDataStore
}
