package com.kg.gettransfer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent

import android.content.Context
import android.content.Intent

import android.media.RingtoneManager

import android.os.Build
import android.os.Handler
import android.os.Looper

import android.support.annotation.CallSuper
import android.support.v4.app.NotificationCompat

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.ui.OffersActivity

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.Screens

import kotlinx.coroutines.Job

import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

import ru.terrakok.cicerone.Router

import timber.log.Timber

class OffersPushService : KoinComponent, FirebaseMessagingService() {
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    private val systemInteractor: SystemInteractor by inject()

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String?) {
        Timber.d("Refreshed token: $token")
        /*
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                it.result?.token?.let {
                    Timber.d("[FCM token]: $it")
                    utils.runAlien { systemInteractor.registerPushToken(it) }
                }
            } else Timber.w("getInstanceId failed", it.exception)
        })
        */
    }
    // [END on_new_token]
}
