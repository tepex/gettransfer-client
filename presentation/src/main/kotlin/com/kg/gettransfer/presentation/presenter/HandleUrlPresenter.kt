package com.kg.gettransfer.presentation.presenter

import android.net.Uri
import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.CHOOSE_OFFER_ID
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.EQUAL
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.FROM_PLACE_ID
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.NEW_TRANSFER
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.OPEN_CHAT
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.PARTNER_CABINET
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.PARTNER_RATE
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
class HandleUrlPresenter : OpenDeepLinkScreenPresenter<HandleUrlView>() {

    lateinit var url: String

    /** TODO: refactor to regular expressions */
    fun handleIntent(appLinkData: Uri) {
        url = appLinkData.toString()
        appLinkData.path?.let { path ->
            when {
                path == PASSENGER_CABINET || path == PARTNER_CABINET -> {
                    appLinkData.fragment?.let { checkPassengerCabinetUrl(it) }
                }
                path.startsWith(PASSENGER_RATE) || path.startsWith(PARTNER_RATE) -> {
                    checkPassengerRateUrl(appLinkData)
                }
                path.contains(VOUCHER) -> {
                    transferId = appLinkData.lastPathSegment?.toLongOrNull()
                    transferId?.let { openVoucherLink(it) }
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
                transferId?.let { id -> openOfferLink(id, offerId, bookNowTransportId) }
                return
            } else if (fragment.contains(OPEN_CHAT)) {
                val transferId = fragment.substring(fragment.indexOf(SLASH) + 1, fragment.indexOf(QUESTION)).toLongOrNull()
                transferId?.let { openChatLink(it) }
                return
            }
            val transferId = fragment.substring(fragment.indexOf(SLASH) + 1).toLongOrNull()
            transferId?.let { openTransferLink(it) }
        }
    }

    private fun checkPassengerRateUrl(appLinkData: Uri) {
        val transferId = appLinkData.lastPathSegment?.toLongOrNull()
        val rate = appLinkData.getQueryParameter(RATE)?.toIntOrNull()
        if (transferId != null && rate != null) {
            openRateTransferLink(transferId, rate)
        }
    }

    private fun openOfferLink(transferId: Long, offerId: Long?, bookNowTransportId: String?) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToPaymentOffer(transferId, offerId, bookNowTransportId))
        } else {
            openOffer(transferId, offerId, bookNowTransportId)
        }
    }

    private fun openChatLink(transferId: Long) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToChat(transferId))
        } else {
            openChat(transferId)
        }
    }

    private fun openTransferLink(transferId: Long) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToShowDetails(transferId))
        } else {
            openTransfer(transferId)
        }
    }

    private fun openRateTransferLink(transferId: Long, rate: Int) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToRateTransfer(transferId, rate))
        } else {
            rateTransfer(transferId, rate)
        }
    }

    private fun openVoucherLink(transferId: Long) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToDownloadVoucher(transferId))
        } else {
            openVoucher(transferId)
        }
    }
}
