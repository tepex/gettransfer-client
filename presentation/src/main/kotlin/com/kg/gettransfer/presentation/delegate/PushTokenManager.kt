package com.kg.gettransfer.presentation.delegate

import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.interactor.OnesignalInteractor

import com.onesignal.OneSignal

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

import org.slf4j.Logger

class PushTokenManager : KoinComponent {

    private val worker: WorkerManager by inject { parametersOf("PushTokenManager") }
    private val log: Logger by inject { parametersOf("GTR-presenter") }

    private val onesignalInteractor: OnesignalInteractor by inject()

    fun registerPushToken() {
        val userId = OneSignal.getPermissionSubscriptionState().subscriptionStatus.userId
        log.debug("OneSignal player_id: $userId")
        userId?.let { playerId ->
            worker.main.launch {
                withContext(worker.bg) { onesignalInteractor.associatePlayerId(playerId) }
            }
        }
    }
}
