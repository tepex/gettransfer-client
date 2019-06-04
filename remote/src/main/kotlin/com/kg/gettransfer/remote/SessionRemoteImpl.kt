package com.kg.gettransfer.remote

import com.kg.gettransfer.data.SessionRemote
import com.kg.gettransfer.data.model.*

import com.kg.gettransfer.remote.mapper.ConfigsMapper
import com.kg.gettransfer.remote.mapper.AccountMapper
import com.kg.gettransfer.remote.mapper.EndpointMapper
import com.kg.gettransfer.remote.mapper.MobileConfigMapper
import com.kg.gettransfer.remote.model.*

import org.koin.core.parameter.parametersOf

import org.koin.standalone.get
import org.koin.standalone.inject
import org.slf4j.Logger

class SessionRemoteImpl : SessionRemote {
    private val core = get<ApiCore>()
    private val configsMapper = get<ConfigsMapper>()
    private val accountMapper = get<AccountMapper>()
    private val endpointMapper = get<EndpointMapper>()
    private val mobileConfigMapper = get<MobileConfigMapper>()
    private val log: Logger by inject { parametersOf("GTR-remote") }

    override suspend fun getConfigs(): ConfigsEntity {
        val response: ResponseModel<ConfigsModel> = core.tryTwice { core.api.getConfigs() }
        return configsMapper.fromRemote(response.data!!)
    }

    override suspend fun getAccount(): AccountEntity? {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice { core.api.getAccount() }
        response.data?.account?.let { return accountMapper.fromRemote(it) }
        return null
    }

    override suspend fun setAccount(accountEntity: AccountEntity): AccountEntity {
        //val response: ResponseModel<AccountModelWrapper> = tryPutAccount(AccountModelWrapper(accountMapper.toRemote(accountEntity)))
        val response: ResponseModel<AccountModelWrapper> =
            core.tryTwice { core.api.putAccount(AccountModelWrapper(accountMapper.toRemote(accountEntity))) }
        return accountMapper.fromRemote(response.data?.account!!)
    }

    /*private suspend fun tryPutAccount(account: AccountModelWrapper): ResponseModel<AccountModelWrapper> {
        return try { core.api.putAccount(account).await() }
        catch (e: Exception) {
            if (e is RemoteException) throw e *//* second invocation *//*
            val ae = core.remoteException(e)
            if (!ae.isInvalidToken()) throw ae

            try { core.updateAccessToken() } catch (e1: Exception) { throw core.remoteException(e1) }
            return try { core.api.putAccount(account).await() } catch (e2: Exception) { throw core.remoteException(e2) }
        }
    }*/

    override suspend fun login(email: String?, phone: String?, password: String): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice { core.api.login(email, phone, password) }
        return accountMapper.fromRemote(response.data?.account!!)
    }

    override suspend fun register(account: RegistrationAccountEntity): AccountEntity {
        //TODO добавить маппер!!
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice {
            core.api.register(
                account = RegistrationAccountEntityWrapper(
                    RegistrationAccount(
                        account.fullName,
                        account.email,
                        account.phone,
                        account.termsAccepted
                    )
                )
            )
        }
        return accountMapper.fromRemote(response.data?.account!!)
    }

    override suspend fun getVerificationCode(email: String?, phone: String?): Boolean {
        val response: ResponseModel<String?> = core.tryTwice { core.api.getVerificationCode(email, phone) }
        return response.error == null
    }

    /*private suspend fun tryLogin(email: String, password: String): ResponseModel<AccountModelWrapper> {
        return try { core.api.login(email, password).await() }
        catch (e: Exception) {
            if (e is RemoteException) throw e *//* second invocation *//*
            val ae = core.remoteException(e)
            if (!ae.isInvalidToken()) throw ae

            try { core.updateAccessToken() } catch (e1: Exception) { throw core.remoteException(e1) }
            return try { core.api.login(email, password).await() } catch (e2: Exception) { throw core.remoteException(e2) }
        }
    }*/

    override fun changeEndpoint(endpoint: EndpointEntity) = core.changeEndpoint(endpointMapper.toRemote(endpoint))

    override suspend fun getMobileConfigs(): MobileConfigEntity {
        val response: MobileConfig = core.tryTwice { core.api.getMobileConfigs() }
        return mobileConfigMapper.fromRemote(response)
    }
}
