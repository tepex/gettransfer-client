package com.kg.gettransfer.remote

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.SystemRemote

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity

import com.kg.gettransfer.remote.mapper.AccountMapper
import com.kg.gettransfer.remote.mapper.ConfigsMapper
import com.kg.gettransfer.remote.mapper.EndpointMapper

import com.kg.gettransfer.remote.model.AccountModel
import com.kg.gettransfer.remote.model.AccountModelWrapper
import com.kg.gettransfer.remote.model.ConfigsModel
import com.kg.gettransfer.remote.model.ResponseModel

class SystemRemoteImpl(private val core: ApiCore,
                       private val configsMapper: ConfigsMapper,
                       private val accountMapper: AccountMapper,
                       private val endpointMapper: EndpointMapper): SystemRemote {
    override suspend fun getConfigs(): ConfigsEntity {
        val response: ResponseModel<ConfigsModel> = core.tryTwice { core.api.getConfigs() }
		return configsMapper.fromRemote(response.data!!)
    }
    
    override suspend fun getAccount(): AccountEntity? {
        val response: ResponseModel<AccountModelWrapper> = core.tryTwice { core.api.getAccount() }
        response.data?.account?.let { return accountMapper.fromRemote(it) }
        return null
    }
    
    override suspend fun setAccount(accountEntity: AccountEntity): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> = tryPutAccount(accountMapper.toRemote(accountEntity))
        return accountMapper.fromRemote(response.data?.account!!)
    }
    
    private suspend fun tryPutAccount(account: AccountModel): ResponseModel<AccountModelWrapper> {
        return try { core.api.putAccount(account).await() }
        catch(e: Exception) {
            if(e is RemoteException) throw e /* second invocation */
            val ae = core.remoteException(e)
            if(!ae.isInvalidToken()) throw ae

            try { core.updateAccessToken() } catch(e1: Exception) { throw core.remoteException(e1) }
            return try { core.api.putAccount(account).await() } catch(e2: Exception) { throw core.remoteException(e2) }
        }
    }
    
    override suspend fun login(email: String, password: String): AccountEntity {
        val response: ResponseModel<AccountModelWrapper> = tryLogin(email, password)
        return accountMapper.fromRemote(response.data?.account!!)
    }
    
    private suspend fun tryLogin(email: String, password: String): ResponseModel<AccountModelWrapper> {
        return try { core.api.login(email, password).await() }
        catch(e: Exception) {
            if(e is RemoteException) throw e /* second invocation */
            val ae = core.remoteException(e)
            if(!ae.isInvalidToken()) throw ae

            try { core.updateAccessToken() } catch(e1: Exception) { throw core.remoteException(e1) }
            return try { core.api.login(email, password).await() } catch(e2: Exception) { throw core.remoteException(e2) }
        }
    }
    
    override fun changeEndpoint(endpoint: EndpointEntity) = core.changeEndpoint(endpointMapper.toRemote(endpoint))
}
