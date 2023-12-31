package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.SessionDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.SessionDataStoreCache
import com.kg.gettransfer.data.ds.SessionDataStoreRemote

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.eventListeners.AccountChangedListener
import com.kg.gettransfer.domain.eventListeners.CreateTransferListener

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.RegistrationAccount
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.User
import com.kg.gettransfer.domain.model.Contact

import com.kg.gettransfer.domain.repository.SessionRepository

import com.kg.gettransfer.sys.domain.ConfigsRepository

import org.koin.core.inject

@Suppress("TooManyFunctions")
class SessionRepositoryImpl(
    private val factory: DataStoreFactory<SessionDataStore, SessionDataStoreCache, SessionDataStoreRemote>
) : BaseRepository(), SessionRepository {

    private val preferencesCache: PreferencesCache by inject()
    private val configsRepository: ConfigsRepository by inject()

    private val accountChangedListeners = mutableSetOf<AccountChangedListener>()
    private val createTransferListeners = mutableSetOf<CreateTransferListener>()

    override var isInitialized = false
        private set

    override var account = Account.EMPTY
        private set(value) {
            field = value
            notifyUpdateAccount()
        }

    override var tempUser = User.EMPTY.copy()

    override var appLanguage: String
        get() = preferencesCache.appLanguage
        set(value) { preferencesCache.appLanguage = value }

    override var isAppLanguageChanged: Boolean
        get() = preferencesCache.isAppLanguageChanged
        set(value) { preferencesCache.isAppLanguageChanged = value }

    @Suppress("ComplexMethod", "ReturnCount")
    override suspend fun coldStart(): Result<Account> {
        /*
        val r = systemRepository.coldStart()
        if (r.error != null) return Result(account, r.error)
        */

        val configs = configsRepository.getResult().getModel()
        account = factory.retrieveCacheDataStore().getAccount()?.map(configs) ?: Account.EMPTY

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

    override suspend fun authOldToken(authKey: String): Result<Unit> {
        retrieveRemoteEntity { factory.retrieveRemoteDataStore().authOldToken(authKey) }
        return Result(Unit)
    }

    override suspend fun putAccount(newAccount: Account, pass: String?, repeatedPass: String?): Result<Account> {
        val accountEntity =
            if (pass != null && repeatedPass != null) {
                newAccount.map().apply {
                    password = pass
                    repeatedPassword = repeatedPass
                }
            } else {
                newAccount.map()
            }

        val result: ResultEntity<AccountEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().setAccount(accountEntity)
        }
        if (result.error == null) {
            result.entity?.let { entity ->
                factory.retrieveCacheDataStore().setAccount(entity)
                account = entity.map(configsRepository.getResult().getModel())
            }
        }
        return Result(account, result.error?.map())
    }

    override suspend fun putNoAccount(newAccount: Account): Result<Account> {
        factory.retrieveCacheDataStore().setAccount(newAccount.map())
        return Result(newAccount)
    }

    override suspend fun login(contact: Contact<String>, password: String, withSmsCode: Boolean): Result<Account> {
        val result: ResultEntity<AccountEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().login(contact.map(), password)
        }
        if (result.error == null) {
            result.entity?.let { entity ->
                factory.retrieveCacheDataStore().setAccount(entity)
                account = entity.map(configsRepository.getResult().getModel())
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
                account = entity.map(configsRepository.getResult().getModel())
            }
        }
        return Result(account, result.error?.map())
    }

    override suspend fun getVerificationCode(contact: Contact<String>): Result<Boolean> {
        val result: ResultEntity<Boolean?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().getVerificationCode(contact.map())
        }
        return Result(result.entity != null && result.entity, result.error?.map())
    }

    override suspend fun logout(): Result<Account> {
        retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().signOut()
        }.error?.let { return Result(account, it.map()) }
        tempUser = User.EMPTY.copy()
        account.user = User.EMPTY.copy()
        account.partner = null
        factory.retrieveCacheDataStore().clearAccount()
        preferencesCache.logout()
        return Result(account)
    }

    override suspend fun getConfirmationCode(contact: Contact<String>): Result<Boolean> {
        val result: ResultEntity<Boolean?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().getConfirmationCode(contact.map())
        }
        return Result(result.entity != null && result.entity, result.error?.map())
    }

    override suspend fun changeContact(contact: Contact<String>, code: String): Result<Boolean> {
        val result: ResultEntity<Boolean?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().changeContact(contact.map(), code)
        }
        if (result.error == null) {
            when (contact) {
                is Contact.EmailContact -> account.user.profile.email = contact.email
                is Contact.PhoneContact -> account.user.profile.phone = contact.phone
            }
        }
        return Result(result.entity != null && result.entity, result.error?.map())
    }

    override fun addAccountChangedListener(listener: AccountChangedListener) {
        accountChangedListeners.add(listener)
    }

    override fun removeAccountChangedListener(listener: AccountChangedListener) {
        accountChangedListeners.remove(listener)
    }

    private fun notifyUpdateAccount() {
        accountChangedListeners.forEach { it.accountChanged() }
    }

    override fun addCreateTransferListener(listener: CreateTransferListener) {
        createTransferListeners.add(listener)
    }

    override fun removeCreateTransferListener(listener: CreateTransferListener) {
        createTransferListeners.remove(listener)
    }

    override fun notifyCreateTransfer() {
        createTransferListeners.forEach { it.onCreateTransferClick() }
    }
}
