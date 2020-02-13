package com.kg.gettransfer.service

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.getOffer
import com.kg.gettransfer.presentation.model.DeeplinkScreenModel
import com.kg.gettransfer.presentation.ui.HandleUrlActivity
import com.kg.gettransfer.presentation.ui.ChatActivity
import com.kg.gettransfer.presentation.ui.OffersActivity
import com.kg.gettransfer.presentation.ui.TransferDetailsActivity
import com.kg.gettransfer.presentation.ui.MainNavigateActivity
import com.kg.gettransfer.presentation.ui.ProfileSettingsActivity
import com.kg.gettransfer.presentation.ui.CreateOrderActivity
import com.kg.gettransfer.presentation.ui.PaymentOfferActivity
import com.kg.gettransfer.presentation.view.ChatView
import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.TransferDetailsView
import com.kg.gettransfer.presentation.view.MainNavigateView
import com.kg.gettransfer.utilities.DeeplinkManager
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import kotlin.reflect.KClass

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

    @Suppress("ComplexMethod")
    private fun checkUrlData(appLinkData: Uri) {
        if (!sessionInteractor.isInitialized) {
            openHandleUrlActivity(appLinkData)
            return
        }

        when (val screen = deeplinkManager.getScreenForLink(appLinkData)) {
            is DeeplinkScreenModel.PaymentOffer ->
                checkPaymentOfferLink(screen.transferId, screen.offerId, screen.bookNowOfferId)
            is DeeplinkScreenModel.Chat -> openChatActivity(screen.transferId)
            is DeeplinkScreenModel.Transfer -> checkTransferLink(screen.transferId)
            is DeeplinkScreenModel.RateTransfer -> openMainActivityForRateTransfer(screen.transferId, screen.rate)
            is DeeplinkScreenModel.DownloadVoucher -> openHandleUrlActivity(appLinkData)
            is DeeplinkScreenModel.CreateOrder -> checkCreateOrderLink(screen)
            is DeeplinkScreenModel.NewPassword -> openActivity(ProfileSettingsActivity::class)
            is DeeplinkScreenModel.Main -> openMainActivity()
        }
    }

    private fun checkPaymentOfferLink(
        transferId: Long,
        offerId: Long?,
        bookNowTransportId: String?
    ) = worker.main.launch {
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
                openActivity(PaymentOfferActivity::class)
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

    private fun checkCreateOrderLink(screen: DeeplinkScreenModel.CreateOrder) = worker.main.launch {
        with(orderInteractor) {
            screen.fromPlaceId?.let { withContext(worker.bg) { updatePoint(false, it) } }
            screen.toPlaceId?.let   { withContext(worker.bg) { updatePoint(true, it) } }
            screen.promo?.let { promoCode = it }
            if (isCanCreateOrder()) {
                openActivity(CreateOrderActivity::class)
            } else {
                openMainActivity()
            }
        }
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

    private fun openHandleUrlActivity(uri: Uri) {
        val intent = Intent(application, HandleUrlActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = uri
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
    }

    private fun openChatActivity(transferId: Long) {
        openActivity(ChatActivity::class, Bundle().apply {
            putLong(ChatView.EXTRA_TRANSFER_ID, transferId)
        })
    }

    private fun openOffersActivity(transferId: Long) {
        openActivity(OffersActivity::class, Bundle().apply {
            putLong(OffersView.EXTRA_TRANSFER_ID, transferId)
        })
    }

    private fun openTransferDetailsActivity(transferId: Long) {
        openActivity(TransferDetailsActivity::class, Bundle().apply {
            putLong(TransferDetailsView.EXTRA_TRANSFER_ID, transferId)
        })
    }

    private fun openMainActivityForRateTransfer(transferId: Long, rate: Int?) {
        openActivity(MainNavigateActivity::class, Bundle().apply {
            putLong(MainNavigateView.EXTRA_RATE_TRANSFER_ID, transferId)
            rate?.let { putInt(MainNavigateView.EXTRA_RATE_VALUE, it) }
        })
    }

    private fun openMainActivity() {
        openActivity(MainNavigateActivity::class)
    }

    private fun <T : Any> openActivity(activity: KClass<T>, extras: Bundle? = null) {
        val intent = Intent(application, activity.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            extras?.let { putExtras(it) }
        }
        application.startActivity(intent)
    }
}
