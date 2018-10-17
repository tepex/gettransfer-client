package com.kg.gettransfer.data.ds

open class RouteDataStoreFactory(private val remoteDataStore: RouteRemoteDataStore) {
    open fun retrieveRemoteDataStore() = remoteDataStore
}
