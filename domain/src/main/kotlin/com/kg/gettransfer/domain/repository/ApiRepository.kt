package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

interface ApiRepository {
	suspend fun updateToken(): String
	suspend fun configs(): Configs
	suspend fun login(): Account
}
