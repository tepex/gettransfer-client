package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.model.map
import com.kg.gettransfer.data.SessionCache
import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.MobileConfigEntity
import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

class SessionCacheImpl : SessionCache, KoinComponent {

    private val db: CacheDatabase by inject()

    override fun getConfigs() = db.configsCachedDao().selectAll().firstOrNull()?.map()

    override fun setConfigs(configs: ConfigsEntity) = db.configsCachedDao().update(configs.map())

    override fun getMobileConfigs() = db.mobileConfigsCachedDao().selectAll().firstOrNull()?.map()

    override fun setMobileConfigs(configs: MobileConfigEntity) = db.mobileConfigsCachedDao().update(configs.map())

    override fun getAccount() = db.accountCachedDao().selectAll().firstOrNull()?.map()

    override fun setAccount(account: AccountEntity): AccountEntity {
        db.accountCachedDao().update(account.map())
        return account
    }

    override suspend fun clearAccount() = db.accountCachedDao().deleteAll()
}
