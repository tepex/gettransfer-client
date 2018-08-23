package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.repository.ApiRepository

class ApiInteractor(private val repository: ApiRepository) {
	suspend fun updateToken(): String = repository.updateToken()
	suspend fun configs(): Configs = repository.configs()
	suspend fun login(): Account = repository.login()
}
