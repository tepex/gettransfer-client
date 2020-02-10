package com.kg.gettransfer.service

import android.app.Application
import android.content.Intent
import androidx.core.net.toUri
import com.kg.gettransfer.presentation.ui.HandleUrlActivity
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal

class OneSignalNotificationOpenedHandler(private val application: Application) : OneSignal.NotificationOpenedHandler {

    override fun notificationOpened(result: OSNotificationOpenResult) {
        result.notification.payload.launchURL
        val intent = Intent(application, HandleUrlActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = result.notification.payload.launchURL.toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
    }
}