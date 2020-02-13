package com.kg.gettransfer.service

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.interactor.*
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.getOffer
import com.kg.gettransfer.presentation.model.DeeplinkScreenModel
import com.kg.gettransfer.presentation.ui.*
import com.kg.gettransfer.presentation.view.*
import com.kg.gettransfer.utilities.DeeplinkManager
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class OneSignalNotificationOpenedHandler(
    private val application: Application
) : OneSignal.NotificationOpenedHandler, KoinComponent {

    val worker: WorkerManager by inject { parametersOf("OneSignalNotificationOpenedHandler") }

    private val deeplinkManager: DeeplinkManager by inject()

    private val sessionInteractor: SessionInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()
    private val offerInteractor: OfferInteractor by inject()
    private val paymentInteractor: PaymentInteractor by inject()

    override fun notificationOpened(result: OSNotificationOpenResult) {
        result.notification.payload.launchURL?.let { url ->
            checkUrlData(url.toUri())
        } ?: openMainActivity()
    }

    private fun checkUrlData(appLinkData: Uri) {
        if (!sessionInteractor.isInitialized) {
            openHandleUrlActivity(appLinkData)
            return
        }

        when(val screen = deeplinkManager.getScreenForLink(appLinkData)) {
            is DeeplinkScreenModel.DownloadVoucher -> openHandleUrlActivity(appLinkData)
            is DeeplinkScreenModel.NewPassword -> openProfileSettingsActivity()
            is DeeplinkScreenModel.Main -> openMainActivity()
            is DeeplinkScreenModel.CreateOrder -> checkCreateOrderLink(screen)

            is DeeplinkScreenModel.PaymentOffer -> checkPaymentOfferLink(screen.transferId, screen.offerId, screen.bookNowOfferId)
            is DeeplinkScreenModel.Chat -> openChatActivity(screen.transferId)
            is DeeplinkScreenModel.Transfer -> checkTransferLink(screen.transferId)
            is DeeplinkScreenModel.RateTransfer -> openMainActivityForRateTransfer(screen.transferId, screen.rate)
        }
    }

    private fun checkCreateOrderLink(screen: DeeplinkScreenModel.CreateOrder) = worker.main.launch {
        with(orderInteractor) {
            screen.fromPlaceId?.let { withContext(worker.bg) { updatePoint(false, it) } }
            screen.toPlaceId?.let   { withContext(worker.bg) { updatePoint(true, it) } }
            screen.promo?.let { promoCode = it }
            if (isCanCreateOrder()) {
                openCreateOrderActivity()
            } else {
                openMainActivity()
            }
        }
    }

    private fun checkPaymentOfferLink(transferId: Long, offerId: Long?, bookNowTransportId: String?) = worker.main.launch {
        getTransfer(transferId)?.let { transfer ->
            val offerItem: OfferItem? = when {
                offerId != null && offerId != 0L ->
                    withContext(worker.bg) { offerInteractor.getOffers(transfer.id) }.model.getOffer(offerId)
                !bookNowTransportId.isNullOrEmpty()      ->
                    transfer.bookNowOffers.find { it.transportType.id.toString() == bookNowTransportId }
                else                                     -> null
            }
            if (offerItem != null) {
                with(paymentInteractor) {
                    selectedTransfer = transfer
                    selectedOffer = offerItem
                }
                openPaymentOfferActivity()
            } else {
                openMainActivity()
            }
        }
    }

    private fun checkTransferLink(transferId: Long) = worker.main.launch {
        getTransfer(transferId)?.let { transfer ->
            if (transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_ACTIVE) {
                openOffersActivity(transferId)
            } else {
                openTransferDetailsActivity(transferId)
            }
        }
    }

    private fun openHandleUrlActivity(uri: Uri) {
        val intent = Intent(application, HandleUrlActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = uri
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
    }

    private fun openProfileSettingsActivity() {
        val intent = Intent(application, ProfileSettingsActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
    }

    private fun openMainActivity() {
        val intent = Intent(application, MainNavigateActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
    }

    private fun openCreateOrderActivity() {
        val intent = Intent(application, CreateOrderActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
    }

    private fun openPaymentOfferActivity() {
        val intent = Intent(application, PaymentOfferActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
    }

    private fun openChatActivity(transferId: Long) {
        val intent = Intent(application, ChatActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(ChatView.EXTRA_TRANSFER_ID, transferId)
        }
        application.startActivity(intent)
    }

    private fun openOffersActivity(transferId: Long) {
        val intent = Intent(application, OffersActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(OffersView.EXTRA_TRANSFER_ID, transferId)
        }
        application.startActivity(intent)
    }

    private fun openTransferDetailsActivity(transferId: Long) {
        val intent = Intent(application, TransferDetailsActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(TransferDetailsView.EXTRA_TRANSFER_ID, transferId)
        }
        application.startActivity(intent)
    }

    private fun openMainActivityForRateTransfer(transferId: Long, rate: Int?) {
        val intent = Intent(application, MainNavigateActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(MainNavigateView.EXTRA_RATE_TRANSFER_ID, transferId)
            putExtra(MainNavigateView.EXTRA_RATE_VALUE, rate)
        }
        application.startActivity(intent)
    }

    private suspend fun getTransfer(transferId: Long): Transfer? {
        val result = withContext(worker.bg) { transferInteractor.getTransfer(transferId) }
        return if (result.isError()) {
            openMainActivity()
            null
        } else {
            result.model
        }
    }
}