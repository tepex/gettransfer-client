package com.kg.gettransfer.service

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.presentation.ui.HandleUrlActivity
import com.kg.gettransfer.presentation.ui.OffersActivity
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.CHOOSE_OFFER_ID
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.OPEN_CHAT
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.PARTNER_CABINET
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.PASSENGER_CABINET
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.SLASH
import com.kg.gettransfer.presentation.view.OffersView
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal
import org.koin.core.KoinComponent
import org.koin.core.inject

class OneSignalNotificationOpenedHandler(
    private val application: Application
) : OneSignal.NotificationOpenedHandler, KoinComponent {

    private val sessionInteractor: SessionInteractor by inject()

    override fun notificationOpened(result: OSNotificationOpenResult) {
        val uri = result.notification.payload.launchURL.toUri()

        val intent = getIntentForUrl(uri)
        application.startActivity(intent)
    }

    private fun getIntentForUrl(uri: Uri): Intent {
        val fragment = uri.fragment ?: return getHandleUrlActivityIntent(uri)

        val isCabinetUrl = uri.path == PASSENGER_CABINET || uri.path == PARTNER_CABINET
        val isOfferPaymentUrl = fragment.contains(CHOOSE_OFFER_ID)
        val isChatUrl = fragment.contains(OPEN_CHAT)

        val transferId = fragment.substring(fragment.indexOf(SLASH) + 1).toLongOrNull()
        val isOpenOffersScreen = isCabinetUrl && !isOfferPaymentUrl && !isChatUrl && transferId != null
        return if (sessionInteractor.isInitialized && isOpenOffersScreen) {
            Intent(application, OffersActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(OffersView.EXTRA_TRANSFER_ID, transferId)
                putExtra(OffersView.EXTRA_ORIGIN, OffersView.SOURCE_NOTIFICATION)
            }
        } else {
            getHandleUrlActivityIntent(uri)
        }
    }

    private fun getHandleUrlActivityIntent(uri: Uri) =
        Intent(application, HandleUrlActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = uri
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
}