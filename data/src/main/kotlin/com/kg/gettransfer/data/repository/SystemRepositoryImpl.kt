package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.PreferencesListener
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.SystemDataStoreCache
import com.kg.gettransfer.data.ds.io.SystemSocketDataStoreOutput
import com.kg.gettransfer.data.ds.SystemDataStoreRemote
import com.kg.gettransfer.data.mapper.*

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.ResultEntity

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.eventListeners.SocketEventListener
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.Locale

import org.koin.standalone.get

class SystemRepositoryImpl(
    private val factory: DataStoreFactory<SystemDataStore, SystemDataStoreCache, SystemDataStoreRemote>,
    private val socketDataStore: SystemSocketDataStoreOutput
) : BaseRepository(), SystemRepository, PreferencesListener {

    private val preferencesCache = get<PreferencesCache>()
    private val configsMapper    = get<ConfigsMapper>()
    private val accountMapper    = get<AccountMapper>()
    private val endpointMapper   = get<EndpointMapper>()
    private val addressMapper    = get<AddressMapper>()
    private val mobileConfMapper = get<MobileConfigMapper>()
    private val locationMapper   = get<LocationMapper>()

    private val socketListeners = mutableSetOf<SocketEventListener>()

    init {
        preferencesCache.addListener(this)
        accountMapper.configs = CONFIGS_DEFAULT
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

    override var lastMainScreenMode: String
        get() = preferencesCache.lastMainScreenMode
        set(value) { preferencesCache.lastMainScreenMode = value }

    override var lastCarrierTripsTypeView: String
        get() = preferencesCache.lastCarrierTripsTypeView
        set(value) { preferencesCache.lastCarrierTripsTypeView = value }

    override var firstDayOfWeek: Int
        get() = preferencesCache.firstDayOfWeek
        set(value) { preferencesCache.firstDayOfWeek = value }

    override var isFirstLaunch: Boolean
        get() = preferencesCache.isFirstLaunch
        set(value) { preferencesCache.isFirstLaunch = value }

    override var isOnboardingShowed: Boolean
        get() = preferencesCache.isOnboardingShowed
        set(value) { preferencesCache.isOnboardingShowed = value }

    override var selectedField: String
        get() = preferencesCache.selectedField
        set(value) { preferencesCache.selectedField = value }

    override var accessToken: String
        get() = preferencesCache.accessToken
        set(value) { preferencesCache.accessToken = value }

    override var userEmail: String
        get() = preferencesCache.userEmail
        set(value) { preferencesCache.userEmail = value }

    override var userPassword: String
        get() = preferencesCache.userPassword
        set(value) { preferencesCache.userPassword = value }

    override val endpoints = preferencesCache.endpoints.map { endpointMapper.fromEntity(it) }

    override var endpoint: Endpoint
        get() = endpointMapper.fromEntity(preferencesCache.endpoint)
        set(value) {
            val endpointEntity = endpointMapper.toEntity(value)
            preferencesCache.endpoint = endpointEntity
        }

    override var favoriteTransportTypes: Set<TransportType.ID>?
        get() = preferencesCache.favoriteTransportTypes
                ?.map { TransportType.ID.parse(it) }
                ?.toSet()
        set(value) {
            preferencesCache.favoriteTransportTypes =
                    value?.map { it.name }
                            ?.toSet()
        }

    override var addressHistory: List<GTAddress>
        get() = preferencesCache.addressHistory.map { addressMapper.fromEntity(it) }
        set(value) { preferencesCache.addressHistory = value.map { addressMapper.toEntity(it) } }

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
            //if (result.error != null) return Result(account, ExceptionMapper.map(result.error))

            account = factory.retrieveCacheDataStore().getAccount()?.let { accountMapper.fromEntity(it) }?: NO_ACCOUNT
            if (result.error != null) {
                configs = factory.retrieveCacheDataStore().getConfigs()?.let { configsMapper.fromEntity(it) }?: CONFIGS_DEFAULT
                return Result(account, ExceptionMapper.map(result.error))
            }
        }

        if (mobileConfig === MOBILE_CONFIGS_DEFAULT) {
            val result: Result<MobileConfig> = retrieveRemoteModel(mobileConfMapper, MOBILE_CONFIGS_DEFAULT) {
                factory.retrieveRemoteDataStore().getMobileConfig()
            }
            mobileConfig = result.model
            if (result.error != null) return Result(account, result.error)
        }

        var error: ApiException? = null
        val result: ResultEntity<AccountEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getAccount() }
        result.entity?.let {
            if(result.error == null) factory.retrieveCacheDataStore().setAccount(it)
            account = accountMapper.fromEntity(it)
        }
        result.error?.let { error = ExceptionMapper.map(it) }
        isInitialized = true
        return Result(account, error)
    }

    override suspend fun putAccount(account: Account): Result<Account> {
        /*val accountEntity = try { factory.retrieveRemoteDataStore().setAccount(accountMapper.toEntity(account)) }
        catch(e: RemoteException) { return Result(account, ExceptionMapper.map(e)) }

        factory.retrieveCacheDataStore().setAccount(accountEntity)
        this.account = accountMapper.fromEntity(accountEntity)
        return Result(this.account)*/

        val result: ResultEntity<AccountEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().setAccount(accountMapper.toEntity(account))
        }
        result.entity?.let {
            if(result.error == null) {
                factory.retrieveCacheDataStore().setAccount(it)
                this.account = accountMapper.fromEntity(it)
            }
        }
        return Result(this.account, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun putNoAccount(account: Account): Result<Account> {
        factory.retrieveCacheDataStore().setAccount(accountMapper.toEntity(account))
        return Result(account)
    }

    override suspend fun login(email: String, password: String): Result<Account> {
        /*val accountEntity = try { factory.retrieveRemoteDataStore().login(email, password) }
        catch(e: RemoteException) { return Result(account, ExceptionMapper.map(e)) }

        factory.retrieveCacheDataStore().setAccount(accountEntity)
        account = accountMapper.fromEntity(accountEntity)
        return Result(account)*/

        val result: ResultEntity<AccountEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().login(email, password)
        }
        result.entity?.let {
            if(result.error == null) {
                factory.retrieveCacheDataStore().setAccount(it)
                account = accountMapper.fromEntity(it)
            }
        }
        return Result(account, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun accountLogin(email: String?, phone: String?, password: String): Result<Account> {
        val result: ResultEntity<AccountEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().accountLogin(email, phone, password)
        }
        result.entity?.let {
            if(result.error == null) {
                factory.retrieveCacheDataStore().setAccount(it)
                account = accountMapper.fromEntity(it)
            }
        }
        return Result(account, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun getVerificationCode(email: String?, phone: String?): Result<Boolean> {
        val result: ResultEntity<Boolean?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().getVerificationCode(email, phone)
        }
        return Result(result.entity != null && result.entity, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun logout(): Result<Account> {
        account = NO_ACCOUNT
        factory.retrieveCacheDataStore().clearAccount()
        preferencesCache.logout()
        return Result(account)
    }

    override suspend fun registerPushToken(provider: PushTokenType, token: String): Result<Unit> {
        return try {
            factory.retrieveRemoteDataStore().registerPushToken(provider.toString(), token)
            Result(Unit)
        } catch (e: RemoteException) { Result(Unit, ExceptionMapper.map(e)) }
    }

    override suspend fun unregisterPushToken(token: String): Result<Unit> {
        return try {
            factory.retrieveRemoteDataStore().unregisterPushToken(token)
            Result(Unit)
        } catch (e: RemoteException) { Result(Unit, ExceptionMapper.map(e)) }
    }

    override fun accessTokenChanged(accessToken: String) {
        connectionChanged()
    }

    override fun endpointChanged(endpointEntity: EndpointEntity) {
        factory.retrieveRemoteDataStore().changeEndpoint(endpointEntity)
        connectionChanged()
    }

    override var appEnters: Int
        get() = preferencesCache.appEnters
        set(value) { preferencesCache.appEnters = value }

    override suspend fun getMyLocation(): Result<Location> {
        return try {
            //factory.retrieveRemoteDataStore().changeEndpoint(EndpointEntity("", "", API_URL_LOCATION))
            val locationEntity = factory.retrieveRemoteDataStore().getMyLocation()
            //factory.retrieveRemoteDataStore().changeEndpoint(preferencesCache.endpoint)
            Result(locationMapper.fromEntity(locationEntity))
        } catch (e: RemoteException) {
            Result(Location(null, null), ExceptionMapper.map(e))
        }
    }

    override fun addSocketListener(listener: SocketEventListener)    { socketListeners.add(listener) }
    override fun removeSocketListener(listener: SocketEventListener) { socketListeners.remove(listener) }

    /* Socket */

    override fun connectSocket()     = socketDataStore.connectSocket(endpointMapper.toEntity(endpoint), accessToken)
    override fun connectionChanged() = socketDataStore.changeConnection(endpointMapper.toEntity(endpoint), accessToken)
    override fun disconnectSocket()  = socketDataStore.disconnectSocket()

    fun notifyAboutConnection()    = socketListeners.forEach { it.onSocketConnected() }
    fun notifyAboutDisconnection() = socketListeners.forEach { it.onSocketDisconnected() }




    companion object {
        private val CONFIGS_DEFAULT = Configs.DEFAULT_CONFIGS

        private val NO_ACCOUNT = Account(
            user         = User(Profile(null, null, null)),
            locale       = Locale.getDefault(),
            currency     = defineNoAccountCurrency(),
            distanceUnit = DistanceUnit.km,
            groups       = emptyList<String>(),
            carrierId    = null
        )
        private val MOBILE_CONFIGS_DEFAULT = MobileConfig(
            pushShowDelay       = 5,
            orderMinimumMinutes = 120,
            termsUrl            = "terms_of_use",
            buildsConfigs       = null
        )

        private fun defineNoAccountCurrency() =
                java.util.Currency.getInstance(Locale.getDefault()).let {
                    com.kg.gettransfer.domain.model.Currency(it.currencyCode, it.symbol)
                            .let { dc ->
                                if (CONFIGS_DEFAULT.supportedCurrencies.contains(dc)) dc
                                else CONFIGS_DEFAULT.supportedCurrencies.first { c -> c.code == "USD" }
                            }
                }
    }
}
