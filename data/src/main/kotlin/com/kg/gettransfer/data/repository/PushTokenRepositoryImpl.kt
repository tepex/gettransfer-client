package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.PushTokenDataStoreRemote
import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.domain.model.PushTokenType
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.PushTokenRepository

class PushTokenRepositoryImpl(private val pushTokenDataStoreRemote: PushTokenDataStoreRemote): PushTokenRepository {

    override suspend fun registerPushToken(provider: PushTokenType, token: String): Result<Unit> {
        return try {
            pushTokenDataStoreRemote.registerPushToken(provider.toString(), token)
            Result(Unit)
        } catch (e: RemoteException) { Result(Unit, ExceptionMapper.map(e)) }
    }

    override suspend fun unregisterPushToken(token: String): Result<Unit> {
        return try {
            pushTokenDataStoreRemote.unregisterPushToken(token)
            Result(Unit)
        } catch (e: RemoteException) { Result(Unit, ExceptionMapper.map(e)) }
    }
}