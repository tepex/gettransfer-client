package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.PreferencesListener
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.SystemDataStoreCache
import com.kg.gettransfer.data.ds.SystemDataStoreRemote

import com.kg.gettransfer.data.mapper.AccountMapper
import com.kg.gettransfer.data.mapper.AddressMapper
import com.kg.gettransfer.data.mapper.ConfigsMapper
import com.kg.gettransfer.data.mapper.EndpointMapper
import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.GTAddressEntity
import com.kg.gettransfer.data.model.ResultEntity

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.SystemListener

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.concurrent.TimeoutException

import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

class SystemRepositoryImpl(private val factory: DataStoreFactory<SystemDataStore, SystemDataStoreCache, SystemDataStoreRemote>):
                    BaseRepository(), SystemRepository, PreferencesListener, KoinComponent {
    private val preferencesCache: PreferencesCache by inject()
    private val configsMapper: ConfigsMapper by inject()
    private val accountMapper: AccountMapper by inject()
    private val endpointMapper: EndpointMapper by inject()
    private val addressMapper: AddressMapper by inject()

    private val listeners = mutableSetOf<SystemListener>()

    init {
        preferencesCache.addListener(this)
    }

    override var configs = Configs.DEFAULT
        private set
    override var account = Account.NO_ACCOUNT
        private set

    override var lastMode: String
        get() = preferencesCache.lastMode
        set(value) { preferencesCache.lastMode = value }

    override var selectedField: String
        get() = preferencesCache.selectedField
        set(value) { preferencesCache.selectedField = value }

    override val accessToken: String
        get() = preferencesCache.accessToken

    override val endpoints = preferencesCache.endpoints.map { endpointMapper.fromEntity(it) }

    override var endpoint: Endpoint
        get() = endpointMapper.fromEntity(preferencesCache.endpoint)
        set(value) {
            val endpointEntity = endpointMapper.toEntity(value)
            preferencesCache.endpoint = endpointEntity
        }

    override var addressHistory: List<GTAddress>
        get() = preferencesCache.addressHistory.map { addressMapper.fromEntity(it) }
        set(value) { preferencesCache.addressHistory = value.map { addressMapper.toEntity(it) } }

    override suspend fun coldStart(): Result<Account> {
        factory.retrieveRemoteDataStore().changeEndpoint(endpointMapper.toEntity(endpoint))

        if(configs === Configs.DEFAULT) {
            val result: ResultEntity<ConfigsEntity?> = retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getConfigs() }
            result.entity?.let {
                /* Save to cache only fresh data from remote */
                if(result.error == null) factory.retrieveCacheDataStore().setConfigs(it)
                configs = configsMapper.fromEntity(it)
                accountMapper.configs = configs
            }
            /* No chance to go further */
            if(result.error != null) return Result(account, ExceptionMapper.map(result.error))
        }

        var error: ApiException? = null
        if(account === Account.NO_ACCOUNT) {
            val result: ResultEntity<AccountEntity?> = retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getAccount() }
            result.entity?.let {
                if(result.error == null) factory.retrieveCacheDataStore().setAccount(it)
                account = accountMapper.fromEntity(it)
            }
            result.error?.let { error = ExceptionMapper.map(it) }
        }
        return Result(account, error)
    }

    override suspend fun putAccount(account: Account): Result<Account> {
        val accountEntity = try { factory.retrieveRemoteDataStore().setAccount(accountMapper.toEntity(account)) }
        catch(e: RemoteException) { return Result(account, ExceptionMapper.map(e)) }

        factory.retrieveCacheDataStore().setAccount(accountEntity)
        this.account = accountMapper.fromEntity(accountEntity)
        return Result(this.account)
    }

    override suspend fun login(email: String, password: String): Result<Account> {
        val accountEntity = try { factory.retrieveRemoteDataStore().login(email, password) }
        catch(e: RemoteException) { return Result(account, ExceptionMapper.map(e)) }

        factory.retrieveCacheDataStore().setAccount(accountEntity)
        account = accountMapper.fromEntity(accountEntity)
        return Result(account)
    }

    override fun logout() {
        account = Account.NO_ACCOUNT
        factory.retrieveCacheDataStore().clearAccount()
        preferencesCache.logout()
    }

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
