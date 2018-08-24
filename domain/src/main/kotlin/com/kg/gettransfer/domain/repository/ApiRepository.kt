package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.*

interface ApiRepository {
    suspend fun getConfigs(): Configs
    suspend fun getAccount(): Account?
	suspend fun login(): Account
}
