@file:Suppress("TooManyFunctions")
package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.PreferencesListener
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.SessionDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.SessionDataStoreCache
import com.kg.gettransfer.data.ds.SessionDataStoreRemote

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.RegistrationAccount
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.User

import com.kg.gettransfer.domain.repository.SessionRepository
import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.Locale

import org.koin.core.inject

class SessionRepositoryImpl(
    private val factory: DataStoreFactory<SessionDataStore, SessionDataStoreCache, SessionDataStoreRemote>
) : BaseRepository(), SessionRepository, PreferencesListener {

    private val preferencesCache: PreferencesCache by inject()
    private val systemRepository: SystemRepository by inject()

    init {
        preferencesCache.addListener(this)
    }

    override val endpoint: Endpoint
        get() = preferencesCache.endpoint.map()

    override var isInitialized = false
        private set

    override var configs = Configs.EMPTY
        private set

    override var account = Account.EMPTY
        private set

    override var tempUser = User.EMPTY.copy()

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
            preferencesCache.favoriteTransportTypes = value?.map { it.name }?.toSet()
        }

    @Suppress("ComplexMethod", "ReturnCount")
    override suspend fun coldStart(): Result<Account> {
        factory.retrieveRemoteDataStore().changeEndpoint(endpoint.map())

        val r = systemRepository.coldStart()
        if (r.error != null) return Result(account, r.error)

        if (configs === Configs.EMPTY) {
            val result: ResultEntity<ConfigsEntity?> = retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getConfigs()
            }
            result.entity?.let { entity ->
                /* Save to cache only fresh data from remote */
                if (result.error == null) factory.retrieveCacheDataStore().setConfigs(entity)
                configs = entity.map()
            }
            /* No chance to go further */
            // if (result.error != null) return Result(account, ExceptionMapper.map(result.error))

            account = factory.retrieveCacheDataStore().getAccount()?.map(configs) ?: Account.EMPTY
            if (result.error != null) {
                configs = factory.retrieveCacheDataStore().getConfigs()?.map() ?: Configs.EMPTY
                return Result(account, result.error.map())
            }
        }

        var error: ApiException? = null
        val result: ResultEntity<AccountEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getAccount()
        }
        result.entity?.let { entity ->
            if (result.error == null) factory.retrieveCacheDataStore().setAccount(entity)
            account = entity.map(configs)
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
            result.entity?.let { entity ->
                factory.retrieveCacheDataStore().setAccount(entity)
                this.account = entity.map(configs)
            }
            if (pass != null && repeatedPass != null) this.userPassword = pass
        }
        return Result(this.account, result.error?.map())
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
            result.entity?.let { entity ->
                factory.retrieveCacheDataStore().setAccount(entity)
                account = entity.map(configs)
            }
            if (!withSmsCode) {
                this.userEmail = email
                this.userPhone = phone
                this.userPassword = password
            }
        }
        return Result(account, result.error?.map())
    }

    override suspend fun register(registerAccount: RegistrationAccount): Result<Account> {
        val result = try {
            ResultEntity(factory.retrieveRemoteDataStore().register(registerAccount.map()))
        } catch (e: RemoteException) {
            ResultEntity(null, e)
        }
        if (result.error == null) {
            result.entity?.let { entity ->
                factory.retrieveCacheDataStore().setAccount(entity)
                account = entity.map(configs)
            }
        }
        return Result(account, result.error?.map())
    }

    override suspend fun getVerificationCode(email: String?, phone: String?): Result<Boolean> {
        val result: ResultEntity<Boolean?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().getVerificationCode(email, phone)
        }
        return Result(result.entity != null && result.entity, result.error?.map())
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
        return Result(result.entity != null && result.entity, result.error?.map())
    }

    override suspend fun changeEmail(email: String, code: String): Result<Boolean> {
        val result: ResultEntity<Boolean?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().changeEmail(email, code)
        }
        if (result.error == null) {
            this.account.user.profile.email = email
            this.userEmail = email
        }
        return Result(result.entity != null && result.entity, result.error?.map())
    }
}
