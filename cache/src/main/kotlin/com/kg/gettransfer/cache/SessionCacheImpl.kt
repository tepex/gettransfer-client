package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.model.map
import com.kg.gettransfer.data.SessionCache
import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

import org.koin.core.inject
import org.koin.core.KoinComponent

class SessionCacheImpl : SessionCache, KoinComponent {

    private val db: CacheDatabase by inject()

    override fun getConfigs() = db.configsCachedDao().selectAll().firstOrNull()?.map()

    override fun setConfigs(configs: ConfigsEntity) = db.configsCachedDao().update(configs.map())

    override fun getAccount() = db.accountCachedDao().selectAll().firstOrNull()?.map()

    override fun setAccount(account: AccountEntity): AccountEntity {
        db.accountCachedDao().update(account.map())
        return account
    }

    override suspend fun clearAccount() = db.accountCachedDao().deleteAll()
}
