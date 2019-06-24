package com.kg.gettransfer.remote

import com.kg.gettransfer.data.SessionRemote
import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.data.model.RegistrationAccountEntity
import com.kg.gettransfer.remote.model.AccountModelWrapper
import com.kg.gettransfer.remote.model.ConfigsModel
import com.kg.gettransfer.remote.model.MobileConfigModel
import com.kg.gettransfer.remote.model.RegistrationAccountEntityWrapper
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.map
import org.koin.core.parameter.parametersOf
import org.koin.standalone.get
import org.koin.standalone.inject
import org.slf4j.Logger

class SessionRemoteImpl : SessionRemote {
    private val core = get<ApiCore>()
    private val log: Logger by inject { parametersOf("GTR-remote") }

    override suspend fun getConfigs(): ConfigsEntity {
        val response: ResponseModel<ConfigsModel> = core.tryTwice { core.api.getConfigs() }
        return response.data!!.map()
    }

    override suspend fun getAccount(): AccountEntity? {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice { core.api.getAccount() }
        return response.data?.account?.map()
    }

    override suspend fun setAccount(accountEntity: AccountEntity): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> =
            core.tryTwice { core.api.putAccount(AccountModelWrapper(accountEntity.map())) }
        return response.data?.account!!.map()
    }

    override suspend fun login(email: String?, phone: String?, password: String): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice { core.api.login(email, phone, password) }
        return response.data?.account!!.map()
    }

    override suspend fun register(account: RegistrationAccountEntity): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice {
            core.api.register(RegistrationAccountEntityWrapper(account.map()))
        }
        return response.data?.account!!.map()
    }

    override suspend fun getVerificationCode(email: String?, phone: String?): Boolean {
        val response: ResponseModel<String?> = core.tryTwice { core.api.getVerificationCode(email, phone) }
        return response.error == null
    }

    override suspend fun getCodeForChangeEmail(email: String): Boolean {
        val response: ResponseModel<String?> = core.tryTwice { core.api.getCodeForChangeEmail(email) }
        return response.error == null
    }

    override suspend fun changeEmail(email: String, code: String): Boolean {
        val response: ResponseModel<String?> = core.tryTwice { core.api.changeEmail(email, code) }
        return response.error == null
    }

    override fun changeEndpoint(endpoint: EndpointEntity) = core.changeEndpoint(endpoint.map())

    override suspend fun getMobileConfigs(): MobileConfigEntity {
        val response: MobileConfigModel = core.tryTwice { core.api.getMobileConfigs() }
        return response.map()
    }
}
