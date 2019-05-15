package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.mapper.AccountEntityMapper
import com.kg.gettransfer.cache.mapper.ConfigsEntityMapper
import com.kg.gettransfer.cache.mapper.MobileConfigsEntityMapper

import com.kg.gettransfer.data.SessionCache

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.MobileConfigEntity

import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

class SessionCacheImpl: SessionCache, KoinComponent {
    private val db: CacheDatabase by inject()
    private val configsMapper: ConfigsEntityMapper by inject()
    private val mobileConfigsMapper: MobileConfigsEntityMapper by inject()
    private val accountMapper: AccountEntityMapper by inject()

    override fun getConfigs() = db.configsCachedDao().selectAll().firstOrNull()?.let { configsMapper.fromCached(it) } 
    override fun setConfigs(configs: ConfigsEntity) = db.configsCachedDao().update(configsMapper.toCached(configs))

    override fun getMobileConfigs() = db.mobileConfigsCachedDao().selectAll().firstOrNull()?.let { mobileConfigsMapper.fromCached(it) }
    override fun setMobileConfigs(configs: MobileConfigEntity) = db.mobileConfigsCachedDao().update(mobileConfigsMapper.toCached(configs))
    
    override fun getAccount() = db.accountCachedDao().selectAll().firstOrNull()?.let { accountMapper.fromCached(it) }
    
    override fun setAccount(account: AccountEntity): AccountEntity {
        db.accountCachedDao().update(accountMapper.toCached(account))
        return account
    }
    
    override suspend fun clearAccount() = db.accountCachedDao().deleteAll()
}
