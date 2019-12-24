package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.OnesignalRepository

class OnesignalInteractor(private val onesignalRepository: OnesignalRepository) {

    suspend fun associatePlayerId(playerId: String): Result<Unit> {
        onesignalRepository.associatePlayerId(playerId)
        return Result(Unit)
    }
}
