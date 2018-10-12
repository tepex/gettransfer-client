package com.kg.gettransfer.data.repository

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.repository.Logging
import com.kg.gettransfer.domain.repository.Preferences
import com.kg.gettransfer.domain.repository.SystemRepository

class SystemRepositoryImpl(private val apiRepository: ApiRepositoryImpl,
                           private val preferences: Preferences,
                           private val logging: Logging): SystemRepository {
    override suspend fun coldStart() = apiRepository.coldStart()
	override fun getConfigs() = apiRepository.getConfigs()
    override fun getAccount() = apiRepository.getAccount()
    override suspend fun putAccount(account: Account) = apiRepository.putAccount(account)
    override suspend fun login(email: String, password: String) = apiRepository.login(email, password)
    override fun logout() = apiRepository.logout()
    override fun changeEndpoint() = apiRepository.setEndpoint()

    override fun getLastMode() = preferences.lastMode
    override fun setLastMode(value: String){
        preferences.lastMode = value
    }

    override fun getEndpoins() = arrayListOf(Preferences.ENDPOINT_PROD, Preferences.ENDPOINT_DEMO)
    override fun getEndpoint() = preferences.endpoint
    override fun setEndpoint(value: String){
        preferences.endpoint = value
    }

    override fun getLogs() = logging.getLogs()
    override fun clearLogs() = logging.clearLogs()
    override fun getLogsFile() = logging.getLogsFile()
}
