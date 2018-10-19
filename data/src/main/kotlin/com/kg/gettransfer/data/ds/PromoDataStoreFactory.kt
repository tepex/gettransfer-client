package com.kg.gettransfer.data.ds

class PromoDataStoreFactory(private val promoRemote: PromoRemoteDataStore){
    fun retrieveRemoteStore() = promoRemote
}