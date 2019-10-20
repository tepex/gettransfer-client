package com.kg.gettransfer.presentation.presenter

import android.net.Uri
import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.createStartChain
import com.kg.gettransfer.extensions.newChainFromMain
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

    /** TODO: refactor to regular expressions */
    @Suppress("ComplexMethod", "NestedBlockDepth", "UnsafeCallOnNullableType", "ReturnCount")
    fun handleIntent(appLinkData: Uri) {
        url = appLinkData.toString()
        val path = appLinkData.path
        when {
            path == PASSENGER_CABINET -> appLinkData.fragment?.let { fragment ->
                if (fragment.startsWith(TRANSFERS)) {
                    if (fragment.contains(CHOOSE_OFFER_ID)) {
                        val transferId =
                            fragment.substring(fragment.indexOf(SLASH) + 1, fragment.indexOf(QUESTION)).toLongOrNull()
                        val offerId =
                            fragment.substring(fragment.lastIndexOf(EQUAL) + 1, fragment.length).toLongOrNull()
                        var bookNowTransportId: String? = null
                        if (offerId == null) {
                            bookNowTransportId = fragment.substring(fragment.lastIndexOf(EQUAL) + 1, fragment.length)
                        }
                        transferId?.let { id -> openOffer(id, offerId, bookNowTransportId) }
                        return
                    } else if (fragment.contains(OPEN_CHAT)) {
                        val chatId = fragment.substring(fragment.indexOf(SLASH) + 1, fragment.indexOf(QUESTION))
                        openChat(chatId)
                        return
                    }
                    val transferId = fragment.substring(fragment.indexOf(SLASH) + 1).toLongOrNull()
                    transferId?.let { openTransfer(it) }
                    return
                }
            }
            path?.startsWith(PASSENGER_RATE)!! -> {
                val transferId = appLinkData.lastPathSegment?.toLongOrNull()
                val rate = appLinkData.getQueryParameter(RATE)?.toIntOrNull()
                if (transferId != null && rate != null) {
                    rateTransfer(transferId, rate)
                }
                return
            }
            path.contains(VOUCHER) -> viewState.downloadVoucher()
            path.contains(NEW_TRANSFER) -> createOrder(
                appLinkData.getQueryParameter(FROM_PLACE_ID),
                appLinkData.getQueryParameter(TO_PLACE_ID),
                appLinkData.getQueryParameter(PROMO_CODE)
            )
            else -> viewState.showWebView(url)
        }
    }

    @Suppress("ComplexMethod")
    private fun openOffer(transferId: Long, offerId: Long?, bookNowTransportId: String?) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.newChainFromMain(Screens.LoginToPaymentOffer(transferId, offerId))
        } else {
            val result = fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
            result.error?.let { e ->
                if (e.isNotFound()) {
                    viewState.setTransferNotFoundError(transferId)
                }
                router.replaceScreen(Screens.MainPassenger())
            }
            result.isSuccess()?.let { transfer ->
                val offerItem: OfferItem? = when {
                    offerId != null            ->
                        fetchData(NO_CACHE_CHECK) { offerInteractor.getOffers(transferId) }?.find { it.id == offerId }
                    bookNowTransportId != null ->
                        transfer.bookNowOffers.find { it.transportType.id.name == bookNowTransportId }
                    else                       -> null
                }
                offerItem?.let { offer ->
                    with(paymentInteractor) {
                        selectedTransfer = transfer
                        selectedOffer = offer
                    }
                    router.createStartChain(Screens.PaymentOffer())
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER", "EmptyFunctionBlock")
    private fun openChat(chatId: String) {}

    private fun openTransfer(transferId: Long) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.createStartChain(Screens.LoginToShowDetails(transferId))
        } else {
            val result = fetchResult(SHOW_ERROR) { transferInteractor.getTransfer(transferId) }
            result.error?.let { e ->
                if (e.isNotFound()) {
                    viewState.setTransferNotFoundError(transferId)
                }
            }
            result.isSuccess()?.let { transfer ->
                if (transfer.checkStatusCategory() == Transfer.STATUS_CATEGORY_CONFIRMED) {
                    router.createStartChain(Screens.Details(transferId))
                } else {
                    router.createStartChain(Screens.Offers(transferId))
                }
            }
        }
    }

    private fun rateTransfer(transferId: Long, rate: Int) = worker.main.launch {
        checkInitialization()
        if (!accountManager.isLoggedIn) {
            router.replaceScreen(Screens.LoginToRateTransfer(transferId, rate))
        } else {
            router.newRootScreen(Screens.MainPassengerToRateTransfer(transferId, rate))
        }
    }

    fun openMainScreen() = router.replaceScreen(Screens.MainPassenger())
}
