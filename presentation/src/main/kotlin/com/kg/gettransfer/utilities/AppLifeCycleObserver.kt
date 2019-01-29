package com.kg.gettransfer.utilities

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v4.content.LocalBroadcastManager
import org.koin.standalone.KoinComponent

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