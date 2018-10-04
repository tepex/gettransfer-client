package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.SystemCacheDataStore
import com.kg.gettransfer.data.ds.SystemRemoteDataStore

import com.kg.gettransfer.data.mapper.AccountMapper
import com.kg.gettransfer.data.mapper.ConfigsMapper

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

import com.kg.gettransfer.domain.repository.SystemRepository

class SystemRepositoryImpl(private val preferencesCache: PreferencesCache,
                           private val factory: DataStoreFactory<SystemCacheDataStore, SystemRemoteDataStore>,
                           private val configsMapper: ConfigsMapper,
                           private val accountMapper: AccountMapper): SystemRepository {
    override lateinit var configs: Configs

    override var lastMode: String
        get() = preferencesCache.lastMode
        set(value) { preferencesCache.lastMode = value }

    override suspend fun coldStart() {
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
