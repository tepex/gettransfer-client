package com.kg.gettransfer.utilities

import android.content.Context
import android.content.ContextWrapper

import com.onesignal.OneSignal
import org.koin.core.KoinComponent

class OneSignalNotificationManager(val context: Context) : ContextWrapper(context), KoinComponent {

    companion object {
        const val OFFER_CHANEL_PREFIX   = "offer_"
        const val MESSAGE_CHANEL_PREFIX = "message_"
    }

    fun clearOfferNotifications(transferId: Long) {
        OneSignal.cancelGroupedNotifications(OFFER_CHANEL_PREFIX.plus(transferId))
    }

    fun clearChatNotifications(transferId: Long) {
        OneSignal.cancelGroupedNotifications(MESSAGE_CHANEL_PREFIX.plus(transferId))
    }
}