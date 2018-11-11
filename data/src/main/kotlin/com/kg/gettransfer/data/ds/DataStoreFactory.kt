package com.kg.gettransfer.data.ds

open class DataStoreFactory<DS, C: DS, R: DS>(private val cache: C, private val remote: R) {
    open fun retrieveCacheDataStore() = cache
    open fun retrieveRemoteDataStore() = remote
    open fun retrieveDataStore(fromRemote: Boolean): DS = if(fromRemote) remote else cache
}
