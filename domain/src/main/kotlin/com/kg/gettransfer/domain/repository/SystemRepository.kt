package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import java.io.File

interface SystemRepository {
    var lastMode: String
        
    suspend fun coldStart()
    fun getConfigs(): Configs
    suspend fun getAccount(): Account
    suspend fun putAccount(account: Account)
    /* Not used
    suspend fun createAccount(account: Account)
    */
    suspend fun login(email: String, password: String): Account
    fun logout()
    fun changeEndpoint()

    fun getLastMode(): String
    fun setLastMode(value: String)

    fun getEndpoins(): ArrayList<String>
    fun getEndpoint(): String
    fun setEndpoint(value: String)

    fun getLogs(): String
    fun clearLogs()
    fun getLogsFile(): File
}
