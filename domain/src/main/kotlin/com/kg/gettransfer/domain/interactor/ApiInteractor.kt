package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.ApiRepository

class ApiInteractor(private val repository: ApiRepository) {
	fun qqq(): String = repository.qqq()
}
