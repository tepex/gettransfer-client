package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.RouteInfo

interface ApiRepository {
    suspend fun getConfigs(): Configs
    suspend fun getAccount(): Account
    suspend fun putAccount(account: Account)
    suspend fun createAccount(account: Account)
	suspend fun login(email: String, password: String): Account
    suspend fun getRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean): RouteInfo
	fun logout()
}
