package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.repository.ApiRepository

class ApiInteractor(private val repository: ApiRepository) {
	suspend fun getConfigs(): Configs = repository.getConfigs()
	suspend fun getAccount(): Account = repository.getAccount()
	suspend fun putAccount(account: Account) { repository.putAccount(account) }
	/* Not used now.
	suspend fun createAccount(account: Account) { repository.createAccount(account) }
	*/
	suspend fun login(email: String, password: String): Account = repository.login(email, password)
	suspend fun getRouteInfo(points: Array<String>, withPrices: Boolean, returnWay: Boolean):
			RouteInfo = repository.getRouteInfo(points, withPrices, returnWay)
	fun logout() { repository.logout() }
	suspend fun getTransfer(): Transfer = repository.getTransfer()
}
