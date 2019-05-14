package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.PushTokenType
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.PushTokenRepository

class PushTokenInteractor(
        private val pushTokenRepository: PushTokenRepository
) {
    private var pushToken: String? = null

    suspend fun registerPushToken(token: String): Result<Unit> {
        pushToken = token
        pushTokenRepository.registerPushToken(PushTokenType.FCM, token)
        return Result(Unit)
    }

    suspend fun unregisterPushToken(): Result<Unit> {
        pushToken?.let { pushTokenRepository.unregisterPushToken(it) } ?: Result(Unit)
        return Result(Unit)
    }
}