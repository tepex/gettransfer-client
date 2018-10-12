package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.data.ds.SystemDataStoreFactory

import com.kg.gettransfer.data.mapper.AccountMapper
import com.kg.gettransfer.data.mapper.ConfigsMapper
import com.kg.gettransfer.data.mapper.EndpointMapper

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.Endpoint

import com.kg.gettransfer.domain.repository.SystemRepository

class SystemRepositoryImpl(private val preferencesCache: PreferencesCache,
                           private val factory: SystemDataStoreFactory,
                           private val configsMapper: ConfigsMapper,
                           private val accountMapper: AccountMapper,
                           private val endpointMapper: EndpointMapper,
                           private val _endpoints: List<Endpoint>): SystemRepository {
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

    override fun logout() = factory.retrieveCacheDataStore().clearAccount()
}
