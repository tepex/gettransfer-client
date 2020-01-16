package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Result

interface OnesignalRepository {
    suspend fun associatePlayerId(playerId: String): Result<Unit>
}
