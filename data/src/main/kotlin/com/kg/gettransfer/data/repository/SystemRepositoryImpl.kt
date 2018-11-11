package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.PreferencesListener
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.SystemDataStoreCache
import com.kg.gettransfer.data.ds.SystemDataStoreRemote

import com.kg.gettransfer.data.mapper.AccountMapper
import com.kg.gettransfer.data.mapper.AddressMapper
import com.kg.gettransfer.data.mapper.ConfigsMapper
import com.kg.gettransfer.data.mapper.EndpointMapper

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.GTAddressEntity

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.InternetNotAvailableException
import com.kg.gettransfer.domain.SystemListener

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.concurrent.TimeoutException

class SystemRepositoryImpl(private val factory: DataStoreFactory<SystemDataStore, SystemDataStoreCache, SystemDataStoreRemote>,
                           private val preferencesCache: PreferencesCache,
                           private val configsMapper: ConfigsMapper,
                           private val accountMapper: AccountMapper,
                           private val endpointMapper: EndpointMapper,
                           private val addressMapper: AddressMapper): BaseRepository(), SystemRepository, PreferencesListener {

    private val listeners = mutableSetOf<SystemListener>()
    
    init {
        preferencesCache.addListener(this)
    }

    override var configs: Configs? = null
    override var lastMode: String
        get() = preferencesCache.lastMode
        set(value) { preferencesCache.lastMode = value }

    override var selectedField: String
        get() = preferencesCache.selectedField
        set(value) { preferencesCache.selectedField = value }

    override val accessToken = preferencesCache.accessToken
    override val endpoints = preferencesCache.endpoints.map { endpointMapper.fromEntity(it) }

    override var endpoint: Endpoint
        get() = endpointMapper.fromEntity(preferencesCache.endpoint)
        set(value) {
            val endpointEntity = endpointMapper.toEntity(value)
            preferencesCache.endpoint = endpointEntity
        }
        
    override var history: List<GTAddress>
        get() = preferencesCache.lastAddresses.map { addressMapper.fromEntity(it) }
        set(value) {
            preferencesCache.lastAddresses = value.map { addressMapper.toEntity(it) }
        }
        
    override suspend fun coldStart() {
        factory.retrieveRemoteDataStore().changeEndpoint(endpointMapper.toEntity(endpoint))

        try {
            val remoteConfigs = factory.retrieveRemoteDataStore().getConfigs()
            val remoteAccount = factory.retrieveRemoteDataStore().getAccount()

            factory.retrieveCacheDataStore().setConfigs(remoteConfigs)
            factory.retrieveCacheDataStore().setAccount(remoteAccount)

            configs = configsMapper.fromEntity(factory.retrieveCacheDataStore().getConfigs())
            accountMapper.configs = configs!!
        } catch(e: Exception) {
            if(e is InternetNotAvailableException || e is ApiException || e is TimeoutException) {
                configs = configsMapper.fromEntity(factory.retrieveCacheDataStore().getConfigs())
                accountMapper.configs = configs!!
            } else throw e
        }

        factory.retrieveRemoteDataStore().changeEndpoint(endpointMapper.toEntity(endpoint))
        configs = configsMapper.fromEntity(factory.retrieveRemoteDataStore().getConfigs())
        accountMapper.configs = configs!!
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
    
    override fun accessTokenChanged(accessToken: String) {
        listeners.forEach { it.connectionChanged(endpoint, accessToken) }
    }
    
    override fun endpointChanged(endpointEntity: EndpointEntity) {
        factory.retrieveRemoteDataStore().changeEndpoint(endpointEntity)
        listeners.forEach { it.connectionChanged(endpoint, accessToken) }
    }
        
    override fun addListener(listener: SystemListener)    { listeners.add(listener) }
    override fun removeListener(listener: SystemListener) { listeners.add(listener) }
}
