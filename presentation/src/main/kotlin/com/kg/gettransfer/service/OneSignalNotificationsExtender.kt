package com.kg.gettransfer.service

import androidx.core.net.toUri
import com.kg.gettransfer.domain.interactor.ChatInteractor
import com.kg.gettransfer.presentation.model.DeeplinkScreenModel
import com.kg.gettransfer.service.OneSignalNotificationOpenedHandler.Companion.TARGET_URL
import com.kg.gettransfer.utilities.DeeplinkManager
import com.onesignal.NotificationExtenderService
import com.onesignal.OSNotificationReceivedResult
import org.koin.core.KoinComponent
import org.koin.core.inject

class OneSignalNotificationsExtender : NotificationExtenderService(), KoinComponent {

    private val deeplinkManager: DeeplinkManager by inject()
    private val chatInteractor: ChatInteractor by inject()

    override fun onNotificationProcessing(notification: OSNotificationReceivedResult): Boolean {
        val targetUrl = notification.payload.additionalData.getString(TARGET_URL)
        return when (val screen = deeplinkManager.getScreenForLink(targetUrl.toUri())) {
            is DeeplinkScreenModel.Chat -> chatInteractor.openedChatTransferId?.let { screen.transferId == it } ?: false
            else -> false
        }
    }
}