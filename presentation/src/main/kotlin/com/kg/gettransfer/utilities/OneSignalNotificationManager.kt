package com.kg.gettransfer.utilities

import android.content.Context
import android.content.ContextWrapper

import com.onesignal.OneSignal
import org.koin.core.KoinComponent

class OneSignalNotificationManager(val context: Context) : ContextWrapper(context), KoinComponent {

    companion object {
        const val OFFER_CHANEL_POSTFIX   = "_offers"
        const val MESSAGE_CHANEL_POSTFIX = "_messages"
    }

    fun clearOfferNotifications(transferId: Long) = clearGroup(transferId, OFFER_CHANEL_POSTFIX)

    fun clearChatNotifications(transferId: Long) = clearGroup(transferId, MESSAGE_CHANEL_POSTFIX)

    private fun clearGroup(transferId: Long, postfix: String) {
        OneSignal.cancelGroupedNotifications(transferId.toString().plus(postfix))
    }
}
