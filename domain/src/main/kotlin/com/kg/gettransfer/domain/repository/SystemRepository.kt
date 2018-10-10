package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

interface SystemRepository {
    suspend fun coldStart()
    fun getConfigs(): Configs
    fun getAccount(): Account
    suspend fun putAccount(account: Account)
    /* Not used
    suspend fun createAccount(account: Account)
    */
    suspend fun login(email: String, password: String): Account
    fun logout()
    fun changeEndpoint()
}
