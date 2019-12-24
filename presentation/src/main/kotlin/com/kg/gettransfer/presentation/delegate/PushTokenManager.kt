package com.kg.gettransfer.presentation.delegate

import com.google.firebase.iid.FirebaseInstanceId

import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.interactor.OnesignalInteractor
import com.kg.gettransfer.domain.interactor.PushTokenInteractor

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

    private val pushTokenInteractor: PushTokenInteractor by inject()
    private val onesignalInteractor: OnesignalInteractor by inject()

    fun registerPushToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.token?.let { token ->
                    worker.main.launch {
                        log.debug("[FCM token]: $token")
                        withContext(worker.bg) { pushTokenInteractor.registerPushToken(token) }
                    }
                }
            } else {
                log.warn("getInstanceId failed", task.exception)
            }
        }

        val playerId = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId()
        log.debug("OneSignal player_id: $playerId")
        playerId?.let { playerId ->
            worker.main.launch {
                withContext(worker.bg) { onesignalInteractor.associatePlayerId(playerId) }
            }
        }
    }

    suspend fun unregisterPushToken() = pushTokenInteractor.unregisterPushToken()
}
