package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.repository.ApiRepository

class ApiInteractor(private val repository: ApiRepository) {
<<<<<<< HEAD
	suspend fun updateToken(): String = repository.updateToken()
	suspend fun configs() { repository.configs() }
<<<<<<< HEAD
=======
	fun qqq(): String = repository.qqq()
>>>>>>> added retrofit and other
=======
>>>>>>> Api.configs() WIP
}
