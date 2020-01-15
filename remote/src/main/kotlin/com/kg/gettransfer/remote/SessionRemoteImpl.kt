package com.kg.gettransfer.remote

import com.kg.gettransfer.data.SessionRemote

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.RegistrationAccountEntity

import com.kg.gettransfer.remote.model.AccountModelWrapper
import com.kg.gettransfer.remote.model.RegistrationAccountEntityWrapper
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.map

import org.koin.core.get

class SessionRemoteImpl : SessionRemote {

    private val core = get<ApiCore>()

    override suspend fun updateOldToken(authKey: String?) {
        core.updateOldAccessToken(authKey)
    }

    override suspend fun getAccount(): AccountEntity? {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice { core.api.getAccount() }
        @Suppress("UnsafeCallOnNullableType")
        return response.data?.account?.map()
    }

    override suspend fun setAccount(accountEntity: AccountEntity): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> =
            core.tryTwice { core.api.putAccount(AccountModelWrapper(accountEntity.map())) }
        @Suppress("UnsafeCallOnNullableType")
        return response.data?.account!!.map()
    }

    override suspend fun login(email: String?, phone: String?, password: String): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice { core.api.login(email, phone, password) }
        @Suppress("UnsafeCallOnNullableType")
        return response.data?.account!!.map()
    }

    override suspend fun register(account: RegistrationAccountEntity): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice {
            core.api.register(RegistrationAccountEntityWrapper(account.map()))
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data?.account!!.map()
    }

    override suspend fun getVerificationCode(email: String?, phone: String?): Boolean {
        val response: ResponseModel<String?> = core.tryTwice { core.api.getVerificationCode(email, phone) }
        return response.error == null
    }

    override suspend fun getConfirmationCode(email: String?, phone: String?): Boolean {
        val response: ResponseModel<String?> = core.tryTwice { core.api.getConfirmationCode(email, phone) }
        return response.error == null
    }

    override suspend fun changeContact(email: String?, phone: String?, code: String): Boolean {
        val response: ResponseModel<String?> = core.tryTwice { core.api.changeContact(email, phone, code) }
        return response.error == null
    }
}
