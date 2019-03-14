package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.dao.AccountCachedDao
import com.kg.gettransfer.cache.dao.ConfigsCachedDao

import com.kg.gettransfer.cache.mapper.AccountEntityMapper
import com.kg.gettransfer.cache.mapper.ConfigsEntityMapper

import com.kg.gettransfer.data.SystemCache

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

class SystemCacheImpl: SystemCache, KoinComponent {
    private val db: CacheDatabase by inject()
    private val configsMapper: ConfigsEntityMapper by inject()
    private val accountMapper: AccountEntityMapper by inject()

    override fun getConfigs() = db.configsCachedDao().selectAll().firstOrNull()?.let { configsMapper.fromCached(it) } 
    override fun setConfigs(configs: ConfigsEntity) = db.configsCachedDao().update(configsMapper.toCached(configs))
    
    override fun getAccount() = db.accountCachedDao().selectAll().firstOrNull()?.let { accountMapper.fromCached(it) }
    
    override fun setAccount(account: AccountEntity): AccountEntity {
        db.accountCachedDao().update(accountMapper.toCached(account))
        return account
    }
    
    override fun clearAccount() = db.accountCachedDao().deleteAll()
}
