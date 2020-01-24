package com.kg.gettransfer.service

import com.appsflyer.AppsFlyerLib
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import com.yandex.metrica.push.firebase.MetricaMessagingService

import org.koin.core.KoinComponent

import timber.log.Timber

class FirebaseManager : KoinComponent, FirebaseMessagingService() {
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
        AppsFlyerLib.getInstance().updateServerUninstallToken(applicationContext, token)
    }
}
