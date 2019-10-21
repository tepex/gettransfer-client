package com.kg.gettransfer.presentation.presenter

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.model.OfferItem

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.CHOOSE_OFFER_ID
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.EQUAL
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.FROM_PLACE_ID
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.NEW_TRANSFER
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.OPEN_CHAT
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.PASSENGER_CABINET
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.PASSENGER_RATE
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.PROMO_CODE
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.QUESTION
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.RATE
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.SLASH
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.TO_PLACE_ID
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.TRANSFERS
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.VOUCHER
import com.kg.gettransfer.presentation.view.HandleUrlView
import com.kg.gettransfer.presentation.view.Screens
import kotlinx.coroutines.launch

@InjectViewState
class HandleUrlPresenter : BaseHandleUrlPresenter<HandleUrlView>() {

    lateinit var url: String
    var transferId: Long? = null

    /** TODO: refactor to regular expressions */
    fun handleIntent(appLinkData: Uri) {
        url = appLinkData.toString()
        appLinkData.path?.let { path ->
            when {
                path == PASSENGER_CABINET -> appLinkData.fragment?.let { checkPassengerCabinetUrl(it) }
                path.startsWith(PASSENGER_RATE) -> checkPassengerRateUrl(appLinkData)
                path.contains(VOUCHER) -> {
                    transferId = appLinkData.lastPathSegment?.toLongOrNull()
                    transferId?.let { openVoucher(it) }
                }
                path.contains(NEW_TRANSFER) -> createOrder(
                    appLinkData.getQueryParameter(FROM_PLACE_ID),
                    appLinkData.getQueryParameter(TO_PLACE_ID),
                    appLinkData.getQueryParameter(PROMO_CODE)
                )
                else -> viewState.showWebView(url)
            }
        }
    }

    /** TODO: refactor to regular expressions */
    private fun checkPassengerCabinetUrl(fragment: String) {
        if (fragment.startsWith(TRANSFERS)) {
            if (fragment.contains(CHOOSE_OFFER_ID)) {
                val transferId =
                    fragment.substring(fragment.indexOf(SLASH) + 1, fragment.indexOf(QUESTION)).toLongOrNull()
                val offerId =
                    fragment.substring(fragment.lastIndexOf(EQUAL) + 1, fragment.length).toLongOrNull()
                val bookNowTransportId =
                    if (offerId == null) {
                        fragment.substring(fragment.lastIndexOf(EQUAL) + 1, fragment.length)
                    } else {
                        null
                    }
                transferId?.let { id -> openOffer(id, offerId, bookNowTransportId) }
                return
            } else if (fragment.contains(OPEN_CHAT)) {
                val transferId = fragment.substring(fragment.indexOf(SLASH) + 1, fragment.indexOf(QUESTION)).toLongOrNull()
                transferId?.let { openChat(it) }
                return
            }
            val transferId = fragment.substring(fragment.indexOf(SLASH) + 1).toLongOrNull()
            transferId?.let { openTransfer(it) }
        }
    }

    private fun checkPassengerRateUrl(appLinkData: Uri) {
        val transferId = appLinkData.lastPathSegment?.toLongOrNull()
        val rate = appLinkData.getQueryParameter(RATE)?.toIntOrNull()
        if (transferId != null && rate != null) {
            rateTransfer(transferId, rate)
        }
    }

    @Suppress("ComplexMethod")
    private fun openOffer(transferId: Long, offerId: Long?, bookNowTransportId: String?) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToPaymentOffer(transferId, offerId, bookNowTransportId))
        } else {
            checkTransfer(transferId).isSuccess()?.let { transfer ->
                if (transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_ACTIVE) {
                    val offerItem: OfferItem? = when {
                        offerId != null            ->
                            fetchData(NO_CACHE_CHECK) { offerInteractor.getOffers(transfer.id) }?.find { it.id == offerId }
                        bookNowTransportId != null ->
                            transfer.bookNowOffers.find { it.transportType.id.name == bookNowTransportId }
                        else                       -> null
                    }
                    if (offerItem != null) {
                        with(paymentInteractor) {
                            selectedTransfer = transfer
                            selectedOffer = offerItem
                        }
                        router.createStartChain(Screens.PaymentOffer())
                    } else {
                        router.createStartChain(Screens.Offers(transfer.id))
                    }
                } else {
                    router.createStartChain(Screens.Details(transfer.id))
                }
            }
        }
    }

    private fun openChat(transferId: Long) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToChat(transferId))
        } else {
            checkTransfer(transferId).isSuccess()?.let { transfer ->
                router.createStartChain(Screens.Chat(transfer.id))
            }
        }
    }

    private fun openTransfer(transferId: Long) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToShowDetails(transferId))
        } else {
            checkTransfer(transferId).isSuccess()?.let { transfer ->
                if (transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_ACTIVE) {
                    router.createStartChain(Screens.Offers(transferId))
                } else {
                    router.createStartChain(Screens.Details(transferId))
                }
            }
        }
    }

    private fun rateTransfer(transferId: Long, rate: Int) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.replaceScreen(Screens.LoginToRateTransfer(transferId, rate))
        } else {
            checkTransfer(transferId).isSuccess()?.let { transfer ->
                router.newRootScreen(Screens.MainPassengerToRateTransfer(transfer.id, rate))
            }
        }
    }

    private fun openVoucher(transferId: Long) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToDownloadVoucher(transferId))
        } else {
            if (checkTransfer(transferId).isSuccess() != null) {
                viewState.downloadVoucher()
            }
        }
    }

    fun downloadVoucher() {
        transferId?.let { downloadManager.downloadVoucher(it) }
        openMainScreen()
    }

    private suspend fun checkTransfer(transferId: Long) =
        fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }.also { result ->
            result.error?.let { e ->
                if (e.isNotFound()) {
                    viewState.setTransferNotFoundError(transferId) { openMainScreen() }
                }
            }
        }

    fun openMainScreen() = router.replaceScreen(Screens.MainPassenger())
}
