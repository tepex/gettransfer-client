package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.PreferencesListener
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.SessionDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.SessionDataStoreCache
import com.kg.gettransfer.data.ds.SessionDataStoreRemote

import com.kg.gettransfer.data.mapper.*
import com.kg.gettransfer.data.model.*

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.domain.repository.SessionRepository

import java.util.Locale

import org.koin.standalone.get

class SessionRepositoryImpl(
    private val factory: DataStoreFactory<SessionDataStore, SessionDataStoreCache, SessionDataStoreRemote>
) : BaseRepository(), SessionRepository, PreferencesListener {

    private val preferencesCache = get<PreferencesCache>()

    init {
        preferencesCache.addListener(this)
    }

    override val endpoint: Endpoint
        get() = preferencesCache.endpoint.map()

    override var isInitialized = false
        private set

    override var configs = CONFIGS_DEFAULT
        private set
    override var account = NO_ACCOUNT
        private set
    override var tempUser = User.EMPTY.copy()
    override var mobileConfig = MOBILE_CONFIGS_DEFAULT
        private set

    override var accessToken: String
        get() = preferencesCache.accessToken
        set(value) {
            preferencesCache.accessToken = value
        }

    override var userEmail: String?
        get() = preferencesCache.userEmail
        set(value) {
            preferencesCache.userEmail = value
        }

    override var userPhone: String?
        get() = preferencesCache.userPhone
        set(value) {
            preferencesCache.userPhone = value
        }

    override var userPassword: String
        get() = preferencesCache.userPassword
        set(value) {
            preferencesCache.userPassword = value
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

    override suspend fun coldStart(): Result<Account> {
        factory.retrieveRemoteDataStore().changeEndpoint(endpoint.map())

        if (configs === CONFIGS_DEFAULT) {
            val result: ResultEntity<ConfigsEntity?> = retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getConfigs()
            }
            result.entity?.let {
                /* Save to cache only fresh data from remote */
                if (result.error == null) factory.retrieveCacheDataStore().setConfigs(it)
                configs = it.map()
            }
            /* No chance to go further */
            //if (result.error != null) return Result(account, ExceptionMapper.map(result.error))

            account = factory.retrieveCacheDataStore().getAccount()?.let { it.map(configs) } ?: NO_ACCOUNT
            if (result.error != null) {
                configs = factory.retrieveCacheDataStore().getConfigs()?.let { it.map() } ?: CONFIGS_DEFAULT
                return Result(account, result.error.map())
            }
        }

        if (mobileConfig === MOBILE_CONFIGS_DEFAULT) {
            val result: ResultEntity<MobileConfigEntity?> = retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getMobileConfigs()
            }
            if (result.error != null && result.entity == null) return Result(account, ExceptionMapper.map(result.error))
            result.entity?.let {
                mobileConfig = it.map()
                if (result.error == null) factory.retrieveCacheDataStore().setMobileConfigs(it)
            }
        }

        var error: ApiException? = null
        val result: ResultEntity<AccountEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getAccount()
        }
        result.entity?.let {
            if (result.error == null) factory.retrieveCacheDataStore().setAccount(it)
            account = it.map(configs)
        }
        result.error?.let { error = it.map() }
        isInitialized = true
        tempUser = User(account.user.profile.copy(), account.user.termsAccepted)
        return Result(account, error)
    }

    override suspend fun putAccount(account: Account, pass: String?, repeatedPass: String?): Result<Account> {
        /*val accountEntity = try { factory.retrieveRemoteDataStore().setAccount(accountMapper.toEntity(account)) }
        catch(e: RemoteException) { return Result(account, ExceptionMapper.map(e)) }

        factory.retrieveCacheDataStore().setAccount(accountEntity)
        this.account = accountMapper.fromEntity(accountEntity)
        return Result(this.account)*/
        val accountEntity =
            if (pass != null && repeatedPass != null) {
                account.map().apply {
                    password = pass
                    repeatedPassword = repeatedPass
                }
            } else {
                account.map()
            }

        val result: ResultEntity<AccountEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().setAccount(accountEntity)
        }
        if (result.error == null) {
            result.entity?.let {
                factory.retrieveCacheDataStore().setAccount(it)
                this.account = it.map(configs)
            }
            if (pass != null && repeatedPass != null) this.userPassword = pass
        }
        return Result(this.account, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun putNoAccount(account: Account): Result<Account> {
        factory.retrieveCacheDataStore().setAccount(account.map())
        return Result(account)
    }

    override suspend fun login(
        email: String?,
        phone: String?,
        password: String,
        withSmsCode: Boolean
    ): Result<Account> {
        val result: ResultEntity<AccountEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().login(email, phone, password)
        }
        if (result.error == null) {
            result.entity?.let {
                factory.retrieveCacheDataStore().setAccount(it)
                account = it.map(configs)
            }
            if (!withSmsCode) {
                this.userEmail = email
                this.userPhone = phone
                this.userPassword = password
            }
        }
        return Result(account, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun register(registerAccount: RegistrationAccount): Result<Account> {
        val result = try {
            ResultEntity(factory.retrieveRemoteDataStore().register(registerAccount.map()))
        } catch (e: RemoteException) {
            ResultEntity(null, e)
        }
        if (result.error == null) {
            result.entity?.let {
                factory.retrieveCacheDataStore().setAccount(it)
                account = it.map(configs)
            }
        }
        return Result(account, result.error?.let { it.map() })
    }

    override suspend fun getVerificationCode(email: String?, phone: String?): Result<Boolean> {
        val result: ResultEntity<Boolean?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().getVerificationCode(email, phone)
        }
        return Result(result.entity != null && result.entity, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun logout(): Result<Account> {
        tempUser = User.EMPTY.copy()
        account.user = User.EMPTY.copy()
        factory.retrieveCacheDataStore().clearAccount()
        preferencesCache.logout()
        return Result(account)
    }

    override fun accessTokenChanged(accessToken: String) {}
    override fun endpointChanged(endpointEntity: EndpointEntity) {
        factory.retrieveRemoteDataStore().changeEndpoint(endpointEntity)
    }

    override suspend fun getCodeForChangeEmail(email: String): Result<Boolean> {
        val result: ResultEntity<Boolean?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().getCodeForChangeEmail(email)
        }
        return Result(result.entity != null && result.entity, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun changeEmail(email: String, code: String): Result<Boolean> {
        val result: ResultEntity<Boolean?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().changeEmail(email, code)
        }
        if (result.error == null) {
            this.account.user.profile.email = email
            this.userEmail = email
        }
        return Result(result.entity != null && result.entity, result.error?.let { ExceptionMapper.map(it) })
    }

    companion object {
        private val CONFIGS_DEFAULT = Configs.DEFAULT_CONFIGS

        private val NO_ACCOUNT = Account(
            user = User.EMPTY.copy(),
            locale = Locale.getDefault(),
            currency = defineNoAccountCurrency(),
            distanceUnit = DistanceUnit.KM,
            groups = emptyList<String>(),
            carrierId = null
        )

        private val MOBILE_CONFIGS_DEFAULT = MobileConfig(
            pushShowDelay = 5,
            orderMinimumMinutes = 120,
            termsUrl = "terms_of_use",
            smsResendDelaySec = 90,
            buildsConfigs = null
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
