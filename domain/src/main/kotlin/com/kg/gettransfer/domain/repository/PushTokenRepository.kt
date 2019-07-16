package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.PushTokenType
import com.kg.gettransfer.domain.model.Result

interface PushTokenRepository {
    suspend fun registerPushToken(provider: PushTokenType, token: String): Result<Unit>
    suspend fun unregisterPushToken(token: String): Result<Unit>
}
