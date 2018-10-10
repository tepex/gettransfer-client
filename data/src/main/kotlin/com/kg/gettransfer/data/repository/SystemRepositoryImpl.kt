package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.repository.SystemRepository

class SystemRepositoryImpl(private val apiRepository: ApiRepositoryImpl): SystemRepository {
    override suspend fun coldStart() = apiRepository.coldStart()
	override fun getConfigs() = apiRepository.getConfigs()
    override fun getAccount() = apiRepository.getAccount()
    override suspend fun putAccount(account: Account) = apiRepository.putAccount(account)
    override suspend fun login(email: String, password: String) = apiRepository.login(email, password)
    override fun logout() = apiRepository.logout()
    override fun changeEndpoint() = apiRepository.setEndpoint()
}
