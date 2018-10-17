package com.kg.gettransfer.data.ds

open class PaymentDataStoreFactory(private val remoteDataStore: PaymentRemoteDataStore) {
    open fun retrieveRemoteDataStore() = remoteDataStore
}
