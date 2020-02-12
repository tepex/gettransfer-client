package com.kg.gettransfer.utilities

import android.content.Context
import android.content.ContextWrapper

import com.onesignal.OneSignal
import org.koin.core.KoinComponent

class OneSignalNotificationManager(val context: Context) : ContextWrapper(context), KoinComponent {

    companion object {
        const val OFFER_CHANEL_POSTFIX   = "_offer"
        const val MESSAGE_CHANEL_POSTFIX = "_message"
    }

    fun clearOfferNotifications(transferId: Long) {
        OneSignal.cancelGroupedNotifications(transferId.toString().plus(OFFER_CHANEL_POSTFIX))
    }

    fun clearChatNotifications(transferId: Long) {
        OneSignal.cancelGroupedNotifications(transferId.toString().plus(MESSAGE_CHANEL_POSTFIX))
    }
}