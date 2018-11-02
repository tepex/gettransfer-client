package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.data.ds.SystemDataStoreFactory
import com.kg.gettransfer.data.mapper.*

import com.kg.gettransfer.data.model.GTAddressEntity
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.InternetNotAvailableException
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.SystemRepository
import java.lang.Exception
import java.util.concurrent.TimeoutException

class SystemRepositoryImpl(private val preferencesCache: PreferencesCache,
                           private val factory: SystemDataStoreFactory,
                           private val configsMapper: ConfigsMapper,
                           private val accountMapper: AccountMapper,
                           private val endpointMapper: EndpointMapper,
                           private val addressMapper: AddressMapper,
                           private val _endpoints: List<Endpoint>): SystemRepository {
    override var configs: Configs? = null

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

    override var isInternetAvailable: Boolean
        get() = preferencesCache.isInternetAvailable
        set(value) {
            preferencesCache.isInternetAvailable = value
            factory.retrieveRemoteDataStore().changeNetworkAvailability(value)
        }

    override suspend fun coldStart() {
        factory.retrieveRemoteDataStore().changeEndpoint(endpointMapper.toEntity(endpoint))

        try {
            val remoteAccount = factory.retrieveRemoteDataStore().getAccount()
            val remoteConfigs = factory.retrieveRemoteDataStore().getConfigs()

            factory.retrieveCacheDataStore().setConfigs(remoteConfigs)
            factory.retrieveCacheDataStore().setAccount(remoteAccount)

            configs = configsMapper.fromEntity(factory.retrieveCacheDataStore().getConfigs())
            accountMapper.configs = configs as Configs
        } catch (e: Exception) {
            if (e is InternetNotAvailableException || e is ApiException || e is TimeoutException) {
                configs = configsMapper.fromEntity(factory.retrieveCacheDataStore().getConfigs())
                accountMapper.configs = configs as Configs
            } else throw e
        }

        /*factory.retrieveRemoteDataStore().changeEndpoint(endpointMapper.toEntity(endpoint))
        configs = configsMapper.fromEntity(factory.retrieveRemoteDataStore().getConfigs())
        accountMapper.configs = configs!!
        val accountEntity = factory.retrieveRemoteDataStore().getAccount()
        factory.retrieveCacheDataStore().setAccount(accountEntity)*/
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
    override fun getHistory(): List<GTAddress> =
            preferencesCache.lastAddresses!!.map { addressMapper.fromEntity(it) }

    override fun setHistory(history: List<GTAddress>) {
        preferencesCache.lastAddresses = history.map { addressMapper.toEntity(it) }
    }

    override fun logout() = factory.retrieveCacheDataStore().clearAccount()
}
