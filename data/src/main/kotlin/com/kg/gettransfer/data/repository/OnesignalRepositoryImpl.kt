package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.OnesignalDataStoreRemote

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.OnesignalRepository

class OnesignalRepositoryImpl(
    private val onesignalDataStoreRemote: OnesignalDataStoreRemote
) : OnesignalRepository {

    override suspend fun associatePlayerId(playerId: String): Result<Unit> {
        return try {
            onesignalDataStoreRemote.associatePlayerId(playerId)
            Result(Unit)
        } catch (e: RemoteException) { Result(Unit) }
    }
}
