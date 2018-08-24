package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

interface ApiRepository {
    suspend fun getConfigs(): Configs
    suspend fun getAccount(): Account?
	suspend fun login(email: String, password: String): Account
}
