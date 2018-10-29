package com.kg.gettransfer.remote

import com.kg.gettransfer.data.PromoRemote
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.model.PromoEntity

import com.kg.gettransfer.remote.mapper.PromoMapper

import com.kg.gettransfer.remote.model.PromoModel
import com.kg.gettransfer.remote.model.ResponseModel

class PromoRemoteImpl(val core: ApiCore, val mapper: PromoMapper): PromoRemote {
    override suspend fun getDiscount(code: String): PromoEntity {
        val response: ResponseModel<String> = tryGetDiscount(code)
        return mapper.fromRemote(response.data!!)
    }

    private suspend fun tryGetDiscount(code: String): ResponseModel<String> {
        return try { core.api.getDiscount(code).await() }
        catch(e: Exception) {
            if(e is RemoteException) throw e
            val ae = core.remoteException(e)
            if(!ae.isInvalidToken()) throw ae
            try { core.updateAccessToken() } catch(e1: Exception) { throw core.remoteException(e1) }
            try { core.api.getDiscount(code).await() } catch(e2: Exception) { throw core.remoteException(e2) }
        }
    }
}
