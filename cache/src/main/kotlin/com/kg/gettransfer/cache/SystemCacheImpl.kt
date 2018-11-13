package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.dao.AccountCachedDao
import com.kg.gettransfer.cache.dao.ConfigsCachedDao

//import com.kg.gettransfer.cache.mapper.AccountEntityMapper
import com.kg.gettransfer.cache.mapper.ConfigsEntityMapper

import com.kg.gettransfer.data.SystemCache

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

class SystemCacheImpl(private val db: CacheDatabase,
                      private val configsMapper: ConfigsEntityMapper/*,
                      private val accountMapper: AccountEntityMapper*/): SystemCache {

    override fun getConfigs() = db.configsCachedDao().getConfigs().firstOrNull()?.let { configsMapper.fromCached(it) } 
    override fun setConfigs(configs: ConfigsEntity) = db.configsCachedDao().setConfigs(configsMapper.toCached(configs))
    
    override fun getAccount(): AccountEntity? = null
    override fun setAccount(account: AccountEntity) {}
    override fun clearAccount() {}
}
