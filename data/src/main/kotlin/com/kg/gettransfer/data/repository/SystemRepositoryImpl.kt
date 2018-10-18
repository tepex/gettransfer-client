package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.data.ds.SystemDataStoreFactory
import com.kg.gettransfer.data.mapper.*

import com.kg.gettransfer.data.model.GTAddressEntity
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.SystemRepository

class SystemRepositoryImpl(private val preferencesCache: PreferencesCache,
                           private val factory: SystemDataStoreFactory,
                           private val configsMapper: ConfigsMapper,
                           private val accountMapper: AccountMapper,
                           private val endpointMapper: EndpointMapper,
                           private val addressMapper: AddressMapper,
                           private val _endpoints: List<Endpoint>): SystemRepository {

    override val accessToken = preferencesCache.accessToken

    override lateinit var configs: Configs

    override var lastMode: String
        get() = preferencesCache.lastMode
        set(value) { preferencesCache.lastMode = value }

    override val endpoints = _endpoints
    
    override var endpoint: Endpoint
        get() = endpoints.find { it.name == preferencesCache.endpoint }!!
        set(value) {
            preferencesCache.endpoint = value.name
            factory.retrieveRemoteDataStore().changeEndpoint(endpointMapper.toEntity(value))
        }

    override suspend fun coldStart() {
        factory.retrieveRemoteDataStore().changeEndpoint(endpointMapper.toEntity(endpoint))
        configs = configsMapper.fromEntity(factory.retrieveRemoteDataStore().getConfigs())
        accountMapper.configs = configs
        val accountEntity = factory.retrieveRemoteDataStore().getAccount()
        factory.retrieveCacheDataStore().setAccount(accountEntity)
    }

    override suspend fun getAccount() = accountMapper.fromEntity(factory.retrieveCacheDataStore().getAccount())

    override suspend fun putAccount(account: Account) {
        val accountEntity = accountMapper.toEntity(account)
        factory.retrieveCacheDataStore().setAccount(accountEntity)
        factory.retrieveRemoteDataStore().setAccount(accountEntity)
    }

    override suspend fun login(email: String, password: String): Account {
        val accountEntity = factory.retrieveRemoteDataStore().login(email, password)
        factory.retrieveCacheDataStore().setAccount(accountEntity)
        return accountMapper.fromEntity(accountEntity)
    }
    override fun getHistory(): List<GTAddress> {

        val result = ArrayList<GTAddress>()
        val entities = preferencesCache.lastAddresses
        for(i in 0 until entities!!.size)
            result.add(addressMapper.fromEntity(entities[i]))
        return result
    }
    override fun setHistory(history: List<GTAddress>) {
        val result = ArrayList<GTAddressEntity>()
        for (i in 0 until history.size) result.add(addressMapper.toEntity(history[i]))
        preferencesCache.lastAddresses = result
    }

    override fun logout() = factory.retrieveCacheDataStore().clearAccount()
}
