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
import com.kg.gettransfer.data.mapper.MobileConfigMapper

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.ResultEntity

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.SystemListener

import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.Currency
import java.util.Locale

import org.koin.standalone.get

class SystemRepositoryImpl(
    private val factory: DataStoreFactory<SystemDataStore, SystemDataStoreCache, SystemDataStoreRemote>
) : BaseRepository(), SystemRepository, PreferencesListener {

    private val preferencesCache = get<PreferencesCache>()
    private val configsMapper    = get<ConfigsMapper>()
    private val accountMapper    = get<AccountMapper>()
    private val endpointMapper   = get<EndpointMapper>()
    private val addressMapper    = get<AddressMapper>()
    private val mobileConfMapper = get<MobileConfigMapper>()

    private val listeners = mutableSetOf<SystemListener>()

    init {
        preferencesCache.addListener(this)
    }

    override var isInitialized = false
        private set

    override var configs = CONFIGS_DEFAULT
        private set
    override var account = NO_ACCOUNT
        private set
    override var mobileConfig = MOBILE_CONFIGS_DEFAULT
        private set

    override var lastMode: String
        get() = preferencesCache.lastMode
        set(value) { preferencesCache.lastMode = value }

    override var isFirstLaunch: Boolean
        get() = preferencesCache.isFirstLaunch
        set(value) { preferencesCache.isFirstLaunch = value }

    override var isOnboardingShowed: Boolean
        get() = preferencesCache.isOnboardingShowed
        set(value) { preferencesCache.isOnboardingShowed = value }

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

    override var eventsCount: Int
        get() = preferencesCache.eventsCount
        set(value) { preferencesCache.eventsCount = value }

    override var transferIds: List<Long>
        get() = preferencesCache.transferIds
        set(value) { preferencesCache.transferIds = value }

    override suspend fun coldStart(): Result<Account> {
        factory.retrieveRemoteDataStore().changeEndpoint(endpointMapper.toEntity(endpoint))

        if (configs === CONFIGS_DEFAULT) {
            val result: ResultEntity<ConfigsEntity?> = retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getConfigs() }
            result.entity?.let {
                /* Save to cache only fresh data from remote */
                if (result.error == null) factory.retrieveCacheDataStore().setConfigs(it)
                configs = configsMapper.fromEntity(it)
                accountMapper.configs = configs
            }
            /* No chance to go further */
            if (result.error != null) return Result(account, ExceptionMapper.map(result.error))
        }

        if (mobileConfig === MOBILE_CONFIGS_DEFAULT) {
            val result: Result<MobileConfig> = retrieveRemoteModel(mobileConfMapper, MOBILE_CONFIGS_DEFAULT) {
                factory.retrieveRemoteDataStore().getMobileConfig()
            }
            mobileConfig = result.model
            if (result.error != null) return Result(account, result.error)
        }

        var error: ApiException? = null
        if(account === NO_ACCOUNT) {
            val result: ResultEntity<AccountEntity?> = retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getAccount() }
            result.entity?.let {
                if(result.error == null) factory.retrieveCacheDataStore().setAccount(it)
                account = accountMapper.fromEntity(it)
            }
            result.error?.let { error = ExceptionMapper.map(it) }
        }
        isInitialized = true
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

    override fun logout(): Result<Account> {
        account = NO_ACCOUNT
        factory.retrieveCacheDataStore().clearAccount()
        preferencesCache.logout()
        return Result(account)
    }

    override suspend fun registerPushToken(provider: PushTokenType, token: String) {
        factory.retrieveRemoteDataStore().registerPushToken(provider.toString(), token)
    }

    override suspend fun unregisterPushToken(token: String) {
        factory.retrieveRemoteDataStore().unregisterPushToken(token)
    }

    override fun accessTokenChanged(accessToken: String) {
        listeners.forEach { it.connectionChanged(endpoint, accessToken) }
    }

    override fun endpointChanged(endpointEntity: EndpointEntity) {
        factory.retrieveRemoteDataStore().changeEndpoint(endpointEntity)
        listeners.forEach { it.connectionChanged(endpoint, accessToken) }
    }

    override var appEnters: Int
        get() = preferencesCache.appEnters
        set(value) { preferencesCache.appEnters = value }

    override fun addListener(listener: SystemListener)    { listeners.add(listener) }
    override fun removeListener(listener: SystemListener) { listeners.add(listener) }

    companion object {
        private val CONFIGS_DEFAULT = Configs(
            transportTypes         = emptyList<TransportType>(),
            paypalCredentials      = PaypalCredentials("", ""),
            availableLocales       = emptyList<Locale>(),
            preferredLocale        = Locale.getDefault(),
            supportedCurrencies    = emptyList<Currency>(),
            supportedDistanceUnits = emptyList<DistanceUnit>(),
            cardGateways           = CardGateways("", null),
            officePhone            = "",
            baseUrl                = ""
        )
        private val NO_ACCOUNT = Account(
            user         = User(Profile(null, null, null)),
            locale       = Locale.getDefault(),
            currency     = Currency.getInstance("USD"),
            distanceUnit = DistanceUnit.km,
            groups       = emptyList<String>(),
            carrierId    = null
        )
        private val MOBILE_CONFIGS_DEFAULT = MobileConfig(
            pushShowDelay       = 5,
            orderMinimumMinutes = 120,
            termsUrl            = "terms_of_use"
        )
    }
}
