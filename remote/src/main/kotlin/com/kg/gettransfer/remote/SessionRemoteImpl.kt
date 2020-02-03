package com.kg.gettransfer.remote

import com.kg.gettransfer.data.SessionRemote

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.RegistrationAccountEntity
import com.kg.gettransfer.data.model.ContactEntity
import com.kg.gettransfer.data.model.EmailContactEntity
import com.kg.gettransfer.data.model.PhoneContactEntity

import com.kg.gettransfer.remote.model.AccountModelWrapper
import com.kg.gettransfer.remote.model.RegistrationAccountEntityWrapper
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.map

import org.koin.core.get

class SessionRemoteImpl : SessionRemote {

    private val core = get<ApiCore>()

    override suspend fun authOldToken(authKey: String) {
        core.authOldAccessToken(authKey)
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

    override suspend fun login(contactEntity: ContactEntity<String>, password: String): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice {
            when(contactEntity) {
                is EmailContactEntity -> core.api.login(email = contactEntity.email, password = password)
                is PhoneContactEntity -> core.api.login(phone = contactEntity.phone, password = password)
            }
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data?.account!!.map()
    }

    override suspend fun signOut(): Boolean {
        val response: ResponseModel<String?> = core.tryTwice { core.api.signOut() }
        return response.error == null
    }

    override suspend fun register(account: RegistrationAccountEntity): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice {
            core.api.register(RegistrationAccountEntityWrapper(account.map()))
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data?.account!!.map()
    }

    override suspend fun getVerificationCode(contactEntity: ContactEntity<String>): Boolean {
        val response: ResponseModel<String?> = core.tryTwice {
            when(contactEntity) {
                is EmailContactEntity -> core.api.getVerificationCode(email = contactEntity.email)
                is PhoneContactEntity -> core.api.getVerificationCode(phone = contactEntity.phone)
            }
        }
        return response.error == null
    }

    override suspend fun getConfirmationCode(contactEntity: ContactEntity<String>): Boolean {
        val response: ResponseModel<String?> = core.tryTwice {
            when(contactEntity) {
                is EmailContactEntity -> core.api.getConfirmationCode(email = contactEntity.email)
                is PhoneContactEntity -> core.api.getConfirmationCode(phone = contactEntity.phone)
            }
        }
        return response.error == null
    }

    override suspend fun changeContact(contactEntity: ContactEntity<String>, code: String): Boolean {
        val response: ResponseModel<String?> = core.tryTwice {
            when(contactEntity) {
                is EmailContactEntity -> core.api.changeContact(email = contactEntity.email, code = code)
                is PhoneContactEntity -> core.api.changeContact(phone = contactEntity.phone, code = code)
            }
        }
        return response.error == null
    }
}
