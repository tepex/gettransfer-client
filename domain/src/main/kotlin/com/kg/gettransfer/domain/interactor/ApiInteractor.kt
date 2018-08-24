package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.domain.repository.ApiRepository

class ApiInteractor(private val repository: ApiRepository) {
	suspend fun getConfigs(): Configs = repository.getConfigs()
}
