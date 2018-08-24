package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.repository.ApiRepository

class ApiInteractor(private val repository: ApiRepository) {
	suspend fun getConfigs(): Configs = repository.getConfigs()
	suspend fun getAccount(): Account? = repository.getAccount()
	suspend fun login(email: String, password: String): Account = repository.login(email, password)
}
