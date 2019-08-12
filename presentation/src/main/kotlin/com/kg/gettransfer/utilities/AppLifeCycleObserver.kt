package com.kg.gettransfer.utilities

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.koin.core.KoinComponent

class AppLifeCycleObserver(context: Context): LifecycleObserver, KoinComponent {
    private var appContext: Context = context

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForeGround() = Handler().postDelayed({ sendStatus(STATE_FOREGROUND) }, 1000)

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackGround() = sendStatus(!STATE_FOREGROUND)

    private fun sendStatus(isStarted: Boolean) =
            LocalBroadcastManager.getInstance(appContext)
                    .sendBroadcast(getIntent(isStarted))

    private fun getIntent(isForeGround: Boolean) =
            Intent(APP_STATE)
                    .apply { putExtra(STATUS, isForeGround) }

    companion object {
        const val APP_STATE        = "lifeCycleStateChanged"
        const val STATUS           = "AppStatus"
        const val STATE_FOREGROUND =  true

    }
}